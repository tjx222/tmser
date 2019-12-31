
package com.tmser.question;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tmser.common.page.PageList;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: QuestionImgSpider.java, v 1.0 2019年3月7日 下午7:05:51 tmser Exp $
 */
public class QuestionImgDbSpider {

  private final static Logger logger = LoggerFactory.getLogger(QuestionImgDbSpider.class);

  private static final Logger noNeedprocessUrlLogger = LoggerFactory.getLogger("noNeedprocessUrlLogger");

  private static final Logger unprocessUrlLogger = LoggerFactory.getLogger("unprocessUrlLogger");

  static String urlPattern = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

  private static ItemDao itemDao;

  static class QuestionImgDbProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setTimeOut(30000).setSleepTime(100);

    @Override
    public void process(Page page) {
      if (page.getStatusCode() == 200) {
        String fileName = (String) page.getRequest().getExtra("fileName");
        String fold = "img";
        if (fileName.endsWith(".mp3") || fileName.endsWith(".m4a") || fileName.endsWith(".mp4")) {
          fold = "media";
        }

        File f = new File("E://xuekeres//" + fold + "//" + fileName);

        if (!f.exists()) {
          byte[] content = page.getRawContent();
          try {
            FileUtils.writeByteArrayToFile(f, content);
          } catch (IOException e) {
            logger.error("", e);
          }
        }

      } else {
        logger.error("can't found mp3 ,pinyinid: {}", page.getRequest().getExtra("fileName"));
      }

    }

    @Override
    public Site getSite() {
      return site;
    }

  }

  public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    itemDao = ctx.getBean(ItemDao.class);
    Map<String, String> urls = parseUrl();
    // Spider sp = Spider.create(new QuestionImgDbProcessor());
    // for (Entry<String, String> urlEntry : urls.entrySet()) {
    // Request rq = new Request(urlEntry.getKey());
    // rq.putExtra("fileName", urlEntry.getValue());
    // sp.addRequest(rq);
    // }
    //
    // Pipeline pipeline = ctx.getBean("questionImgPipeline", Pipeline.class);
    // sp.addPipeline(pipeline).thread(20).run();
  }

  private static Map<String, String> parseUrl() {
    Item item = new Item();
    Integer maxId = 0;
    item.addCustomCulomn("id,content,answer,exp,sourceId");
    item.buildCondition(" and id > :maxId").put("maxId", maxId);
    item.setOrder(" id asc");
    item.getPage().setNeedTotal(false);
    item.pageSize(1000);
    Map<String, String> urlSet = new HashMap<>(400000);
    try {
      PageList<Item> items = itemDao.listPage(item);
      while (!items.getDatalist().isEmpty()) {
        for (Item it : items.getDatalist()) {
          // parseResUrl(it, urlSet);
          parseXuekeResUrl(it, urlSet); // 处理学科资源
          maxId = it.getId();
        }
        item.customCondition().put("maxId", maxId);
        items = itemDao.listPage(item);
      }
    } catch (Exception e) {
      logger.error("", e);
    }

    return urlSet;
  }

  /**
   * @param it
   * @param urlSet
   */
  private static void parseResUrl(Item it, Map<String, String> urlSet) {
    Html html = new Html(it.getContent() + it.getExp() + it.getAnswer());
    List<String> urls = html.regex(urlPattern, 0).all();
    for (String url : urls) {
      String uriName = url.substring(url.lastIndexOf('/') + 1);
      String fileName = uriName.lastIndexOf('?') < 0 ? uriName : uriName.substring(0, uriName.lastIndexOf('?'));
      fileName = it.getId() + " --- " + fileName.toLowerCase();
      if (fileName.endsWith(".mp3") || fileName.endsWith(".m4a") || fileName.endsWith(".flv")
          || fileName.endsWith(".jpg") || fileName.endsWith(".svg") || fileName.endsWith(".gif")
          || fileName.endsWith(".png") || fileName.endsWith(".jpeg") || fileName.endsWith(".mp4")) {
        urlSet.put(fileName, url);
        unprocessUrlLogger.info("{} --- {}", it.getId(), url);
      } else {
        noNeedprocessUrlLogger.info("{} --- {}", it.getId(), url);
      }

    }

  }

  private static void parseXuekeResUrl(Item it, Map<String, String> urlSet) {
    Html html = new Html(it.getContent() + it.getExp() + it.getAnswer());
    List<String> urls = html.xpath("//img/@src").all();
    for (String url : urls) {
      // String uriName = url.substring(url.lastIndexOf('/') + 1);
      // String fileName = uriName.lastIndexOf('?') < 0 ? uriName : uriName.substring(0, uriName.lastIndexOf('?'));
      // fileName = it.getId() + " --- " + fileName.toLowerCase();
      if (urlSet.get(it.getSourceId()) != null) {
        continue;
      }
      urlSet.put(it.getSourceId(), url);
      if (url.startsWith("Upload")) {
        unprocessUrlLogger.info("{} --- {}", it.getId(), url);
      } else {
        noNeedprocessUrlLogger.info("{} --- {}", it.getId(), url);
      }

    }

  }

}
