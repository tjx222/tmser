
package com.tmser.question;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
public class LixueQuestionImgSpider {

  private final static Logger logger = LoggerFactory.getLogger(LixueQuestionImgSpider.class);

  static class LixueQuestionImgProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setTimeOut(30000).setSleepTime(100);

    @Override
    public void process(Page page) {
      if (page.getStatusCode() == 200) {
        String fileName = (String) page.getRequest().getExtra("fileName");
        String fold = "img";
        if (fileName.endsWith(".mp3") || fileName.endsWith(".m4a")) {
          fold = "media";
        } else if (fileName.endsWith(".ggb")) {
          fold = "ggb";
        }

        File f = new File("E://qres//xuekeres//" + fold + "//" + fileName);

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
    Map<String, String> urls = parseUrl();
    Spider sp = Spider.create(new LixueQuestionImgProcessor());
    for (Entry<String, String> urlEntry : urls.entrySet()) {

      Request rq = new Request(urlEntry.getKey());
      rq.putExtra("fileName", urlEntry.getValue());
      sp.addRequest(rq);
    }

    Pipeline pipeline = ctx.getBean("questionImgPipeline", Pipeline.class);
    sp.addPipeline(pipeline).thread(20).run();
  }

  private static Map<String, String> parseUrl() {
    File imgFile = new File("E://qres/question-miss-meta-log-2019-03-14.log");
    Map<String, String> urlSet = new HashMap<>(200000);
    try {
      for (String url : FileUtils.readLines(imgFile)) {
        String uriName = url.substring(url.lastIndexOf('/') + 1);
        String fileName = uriName.lastIndexOf('?') < 0 ? uriName : uriName.substring(0, uriName.lastIndexOf('?'));
        String fold = "img";
        if (fileName.endsWith(".mp3") || fileName.endsWith(".m4a")) {
          fold = "media";
        } else if (fileName.endsWith(".ggb")) {
          fold = "new//ggb";
        }

        File f = new File("E://lixue//" + fold + "//" + fileName);
        if (!f.exists()) {
          urlSet.put(url, fileName);
        }
      }
    } catch (IOException e) {
      logger.error("", e);
    }

    return urlSet;
  }
}
