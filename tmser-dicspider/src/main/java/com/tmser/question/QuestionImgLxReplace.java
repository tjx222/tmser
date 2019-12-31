
package com.tmser.question;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class QuestionImgLxReplace {

  private final static Logger logger = LoggerFactory.getLogger(QuestionImgLxReplace.class);

  private static final Logger noNeedprocessUrlLogger = LoggerFactory.getLogger("noNeedprocessUrlLogger");

  private static final Logger unprocessUrlLogger = LoggerFactory.getLogger("unprocessUrlLogger");

  // private static ItemDao itemDao;

  private static Item2Dao item2Dao;

  private static XkItemDao xkitemDao;

  private static String talCdnHost = "http://cdn.talcloud.com/cdn/oldques/lximg/";
  static int count = 0;

  public static String int2chineseNum(int src) {
    final String num[] = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
    final String unit[] = { "", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千" };
    String dst = "";
    int count = 0;
    while (src > 0) {
      dst = (num[src % 10] + unit[count]) + dst;
      src = src / 10;
      count++;
    }
    if (dst.startsWith("一十")) {
      dst = dst.replaceFirst("一十", "十");
    }
    return dst.replaceAll("零[千百十]", "零").replaceAll("零+万", "万").replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
        .replaceAll("零+", "零").replaceAll("零$", "");
  }

  public static void main(String[] args) {

    System.out.println(int2chineseNum(1));
    System.out.println(int2chineseNum(12));
    System.out.println(int2chineseNum(10));
    System.out.println(int2chineseNum(101));
    System.out.println(int2chineseNum(1010));
    System.out.println(int2chineseNum(1001));
    System.out.println(int2chineseNum(1023001001));
    System.out.println(int2chineseNum(2001010111));
    // ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    // itemDao = ctx.getBean(ItemDao.class);
    // item2Dao = ctx.getBean(Item2Dao.class);
    // xkitemDao = ctx.getBean(XkItemDao.class);
    // parseUrl();
  }

  private static void parseUrl() {
    Item2 item = new Item2();
    Integer maxId = 0;
    item.buildCondition(" and id > :maxId").put("maxId", maxId);
    item.setOrder(" id asc");
    item.getPage().setNeedTotal(false);
    item.pageSize(1000);
    List<XkItem> xkItemList;

    try {
      PageList<Item2> items = item2Dao.listPage(item);
      while (!items.getDatalist().isEmpty()) {
        logger.info("-------- process start id:{} -- {}", maxId, count);
        xkItemList = new ArrayList<>();
        for (Item2 it : items.getDatalist()) {
          maxId = it.getId();
          parseXuekeResUrl(it, xkItemList); // 处理学科资源
        }
        item.customCondition().put("maxId", maxId);
        items = item2Dao.listPage(item);
        if (!xkItemList.isEmpty()) {
          xkitemDao.batchInsert(xkItemList);
        }
      }
      logger.info("-------- process end id:{} -- {}", maxId, count);

    } catch (Exception e) {
      logger.error("", e);
    }
  }

  private static void parseXuekeResUrl(Item2 it, List<XkItem> urlSet) {
    XkItem xi = new XkItem();
    xi.setSourceId(it.getSourceId());
    XkItem ckXkItem = xkitemDao.getOne(xi);
    if (ckXkItem != null) {
      logger.info("-------- rep id:{}", it.getSourceId());
      return;
    }

    try {
      xi.setBankId(it.getBankId());
      xi.setDifficult(it.getDifficult());
      xi.setOptionCount(it.getOptionCount());
      xi.setTypeId(it.getTypeId());
      xi.setSourceTypeId(it.getId());
      xi.setAnswer(processContent(it, it.getAnswer()));
      xi.setContent(processContent(it, it.getContent()));
      xi.setExp(processContent(it, it.getExp()));

      urlSet.add(xi);
      count++;
    } catch (IllegalStateException e) {
      logger.info("ignore question id : {}, sourse_id : {}", it.getId(), it.getSourceId());
    }
  }

  private static String processContent(Item2 it, String content) {
    Document doc = Jsoup.parse(content);
    Elements imgs = doc.select("img");
    for (Element img : imgs) {
      String src = img.attr("src").trim();
      String filename = Encodes.md5(src) + src.substring(src.lastIndexOf('.'));
      if (src.startsWith("http")) {
        unprocessUrlLogger.info("{} --- {}", it.getSourceId(), src);
      } else {
        noNeedprocessUrlLogger.info("{} --- {}", it.getSourceId(), src);
        throw new IllegalStateException("invalid src");
      }
      img.attr("src", talCdnHost + filename);
    }

    return doc.body().html().replaceAll("\r", "").replaceAll("\n", "");

  }

}
