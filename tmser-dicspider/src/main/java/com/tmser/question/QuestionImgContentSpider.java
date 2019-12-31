
package com.tmser.question;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tmser.utils.Encodes;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: QuestionImgSpider.java, v 1.0 2019年3月7日 下午7:05:51 tmser Exp $
 */
public class QuestionImgContentSpider {

  private final static Logger logger = LoggerFactory.getLogger(QuestionImgContentSpider.class);

  private static final Logger noNeedprocessUrlLogger = LoggerFactory.getLogger("noNeedprocessUrlLogger");

  private static final Logger unprocessUrlLogger = LoggerFactory.getLogger("unprocessUrlLogger");

  static String urlPattern = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

  static class QuestionImgDbProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setTimeOut(30000).setSleepTime(100);

    @Override
    public void process(Page page) {
      if (page.getStatusCode() == 200) {
        String fileName = (String) page.getRequest().getExtra("fileName");
        String fold = "img26";
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
        noNeedprocessUrlLogger.info("{}", page.getRequest().getUrl());
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
    Map<String, String> urls = parseUrl();
    Spider sp = Spider.create(new QuestionImgDbProcessor());
    for (Entry<String, String> urlEntry : urls.entrySet()) {
      Request rq = new Request(urlEntry.getKey());
      rq.putExtra("fileName", urlEntry.getValue());
      sp.addRequest(rq);
    }

    Pipeline pipeline = ctx.getBean("questionImgPipeline", Pipeline.class);
    sp.addPipeline(pipeline).thread(20).run();
  }

  private static Map<String, String> parseUrl() {
    File imgFile = new File("E://xuekeres//unprocess-2019-07-26-1.log");
    Map<String, String> urlSet = new HashMap<>(820000);
    String host = "http://static.zujuan.xkw.com/";
    Set<String> files = new HashSet<>(40000);
    try {
      for (String url : FileUtils.readLines(imgFile)) {
        String uriName = url.substring(url.indexOf("--- ") + 4);
        String fileName = Encodes.md5(uriName) + uriName.substring(uriName.lastIndexOf('.'));
        File f = new File("E://xuekeres//img26//" + fileName);
        if (!f.exists()) {
          if (uriName.startsWith("http")) {
            urlSet.put(uriName, fileName);
          } else {
            urlSet.put(host + uriName, fileName);
          }

        }
        if (files.contains(fileName)) {
          unprocessUrlLogger.info("{} -- {}", url, fileName);
        }
        files.add(fileName);
      }
    } catch (IOException e) {
      logger.error("", e);
    }

    return urlSet;
  }

}
