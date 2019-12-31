
package com.tmser.question;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tmser.common.page.PageList;
import com.tmser.utils.Encodes;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: QuestionImgSpider.java, v 1.0 2019年3月7日 下午7:05:51 tmser Exp $
 */
public class QuestionImgXkReplace {

  private final static Logger logger = LoggerFactory.getLogger(QuestionImgXkReplace.class);

  private static final Logger noNeedprocessUrlLogger = LoggerFactory.getLogger("noNeedprocessUrlLogger");

  private static final Logger unprocessUrlLogger = LoggerFactory.getLogger("unprocessUrlLogger");

  static String urlPattern = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

  // private static ItemDao itemDao;

  private static Item1Dao item1Dao;

  private static XkItemDao xkitemDao;

  private static String talCdnHost = "http://cdn.tmser.com/cdn/oldques/img/";

  public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    // itemDao = ctx.getBean(ItemDao.class);
    item1Dao = ctx.getBean(Item1Dao.class);
    xkitemDao = ctx.getBean(XkItemDao.class);
    parseUrl();
  }

  private static void parseUrl() {
    Item1 item = new Item1();
    Integer maxId = 0;
    item.buildCondition(" and id > :maxId").put("maxId", maxId);
    item.setOrder(" id asc");
    item.getPage().setNeedTotal(false);
    item.pageSize(1000);
    List<XkItem> xkItemList;

    Map<String, String> sps = loadSpecialImg();
    try {
      PageList<Item1> items = item1Dao.listPage(item);
      while (!items.getDatalist().isEmpty()) {
        xkItemList = new ArrayList<>();
        for (Item1 it : items.getDatalist()) {
          maxId = it.getId();
          if (11 == it.getSourceTypeId()) {
            continue;
          }
          parseXuekeResUrl(it, sps, xkItemList); // 处理学科资源
        }
        item.customCondition().put("maxId", maxId);
        items = item1Dao.listPage(item);
        if (!xkItemList.isEmpty()) {
          xkitemDao.batchInsert(xkItemList);
        }
      }
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  private static Map<String, String> loadSpecialImg() {
    File imgFile = new File("E://xuekeres//unprocess-2019-07-22-1.log");
    Map<String, String> urlSet = new HashMap<>(50);
    String host = "http://static.zujuan.xkw.com/";
    try {
      for (String url : FileUtils.readLines(imgFile)) {
        String uriName = url.substring(url.indexOf("--- ") + 4);
        if (!uriName.startsWith("http")) {
          uriName = host + uriName;
        }
        String fileName = Encodes.md5(uriName) + uriName.substring(uriName.lastIndexOf('.'));
        urlSet.put(uriName, fileName);
      }
    } catch (IOException e) {
      logger.error("", e);
    }

    return urlSet;
  }

  private static void parseXuekeResUrl(Item1 it, Map<String, String> sps, List<XkItem> urlSet) {
    XkItem xi = new XkItem();
    xi.setSourceId(it.getSourceId());
    XkItem ckXkItem = xkitemDao.getOne(xi);
    if (ckXkItem != null) {
      return;
    }

    xi.setBankId(it.getBankId());
    xi.setDifficult(it.getDifficult());
    xi.setOptionCount(it.getOptionCount());
    xi.setTypeId(it.getTypeId());
    xi.setSourceTypeId(it.getId());
    xi.setAnswer(processContent(it, it.getAnswer(), sps));
    xi.setContent(processContent(it, it.getContent(), sps));
    xi.setExp(processContent(it, it.getExp(), sps));

    urlSet.add(xi);
  }

  private static String processContent(Item1 it, String content, Map<String, String> sps) {
    Document doc = Jsoup.parse(content);
    Elements imgs = doc.select("img");
    File folder = new File("E:\\xuekeres\\img");
    for (Element img : imgs) {
      String src = img.attr("src");
      String filename;
      if (sps.containsKey(src)) {
        filename = sps.get(src);
      } else {
        filename = Encodes.md5(src) + src.substring(src.lastIndexOf('.'));
      }
      img.attr("src", talCdnHost + filename);
      if (!new File(folder, filename).exists()) {
        unprocessUrlLogger.info("{} --- {}", it.getId(), src);
      }
    }

    return doc.body().html();

  }

}
