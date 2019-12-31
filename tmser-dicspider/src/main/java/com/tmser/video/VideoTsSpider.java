
package com.tmser.video;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;

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
 * @version $Id: VideoSpider.java, v 1.0 2019年11月7日 下午3:08:30 tmser Exp $
 */
public class VideoTsSpider {
  private final static Logger logger = LoggerFactory.getLogger(VideoTsSpider.class);
  private static String m3u8CdnHost = "http://pic.test.com";
  static AtomicInteger counter = new AtomicInteger(0);
  static AtomicInteger failed = new AtomicInteger(0);
  static int total = 0;

  static class VideoTsSpiderProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setTimeOut(30000).setSleepTime(100);

    @Override
    public void process(Page page) {
      if (page.getStatusCode() == 200) {
        String fileName = (String) page.getRequest().getExtra("fileName");
        File f = new File("E:/qres/video/qingke" + fileName);

        if (!f.exists()) {
          byte[] content = page.getRawContent();
          try {
            FileUtils.forceMkdir(f.getParentFile());
            FileUtils.writeByteArrayToFile(f, content);
            int c = counter.addAndGet(1);
            logger.info("total: {} ,aready download:{}, remain:{}, fileName:{}", total, c, total - c, fileName);
          } catch (IOException e) {
            logger.error("", e);
          }
        }

      } else {
        logger.info("download failed count: {}, filename:{}", failed.addAndGet(1),
            page.getRequest().getExtra("fileName"));
      }

    }

    @Override
    public Site getSite() {
      return site;
    }

  }

  public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    Map<String, JSONObject> urls = parseUrl();
    total = urls.size();
    Spider sp = Spider.create(new VideoTsSpiderProcessor());
    for (Entry<String, JSONObject> urlEntry : urls.entrySet()) {
      JSONObject val = urlEntry.getValue();
      Request rq = new Request(urlEntry.getKey());
      rq.putExtra("fileName", val.getString("name"));
      sp.addRequest(rq);
    }

    Pipeline pipeline = ctx.getBean("questionImgPipeline", Pipeline.class);
    sp.addPipeline(pipeline).thread(20).run();
  }

  private static Map<String, JSONObject> parseUrl() {
    File fileFolder = new File("E:\\qres\\video\\qingke");
    Map<String, JSONObject> urlMap = new HashMap<>(300);
    for (File f : fileFolder.listFiles(file -> file.isDirectory())) {
      String courseId = f.getName();
      for (File lectFile : f.listFiles(file -> file.isDirectory())) {
        String lectureId = lectFile.getName();
        for (File m3u8File : lectFile
            .listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".m3u8"))) {
          try {
            for (String tsLine : FileUtils.readLines(m3u8File)) {
              if (tsLine.trim().startsWith("/")) {
                String videoUrl = m3u8CdnHost + tsLine;
                String m3u8Name = "/" + courseId + "/" + lectureId + tsLine;

                File ts = new File(fileFolder, m3u8Name);
                if (!ts.exists()) {
                  JSONObject vinfo = new JSONObject();
                  vinfo.put("name", m3u8Name);
                  urlMap.put(videoUrl, vinfo);
                }
              }
            }
          } catch (IOException e) {
            logger.error("", e);
          }
        }
      }

    }
    return urlMap;
  }

}
