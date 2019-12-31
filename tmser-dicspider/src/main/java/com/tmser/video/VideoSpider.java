
package com.tmser.video;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tmser.utils.Identities;

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
public class VideoSpider {
  private final static Logger logger = LoggerFactory.getLogger(VideoSpider.class);
  static String videoInfo = "http://www.test.com/v2f/resource/qing-detail-new?username=13821540799&id=";
  private static String talCdnHost = "http://cdn.test.com/cdn/qingcourse/img/";

  static class VideoSpiderProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setTimeOut(30000).setSleepTime(100);

    @Override
    public void process(Page page) {
      if (page.getStatusCode() == 200) {
        String fold = (String) page.getRequest().getExtra("folder");
        String fileName = (String) page.getRequest().getExtra("fileName");
        File f = new File("E://qres//video//qingke//" + fold + "//" + fileName);

        if (!f.exists()) {
          byte[] content = page.getRawContent();
          try {
            FileUtils.forceMkdir(f.getParentFile());
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
    Map<String, JSONObject> urls = parseUrl();
    Spider sp = Spider.create(new VideoSpiderProcessor());
    for (Entry<String, JSONObject> urlEntry : urls.entrySet()) {
      JSONObject val = urlEntry.getValue();
      // 缩略图
      // Request rq = new Request(val.getString("snapshot"));
      // rq.putExtra("fileName", FileUtils.getFile(val.getString("snapshot")).getName());
      // rq.putExtra("folder", urlEntry.getKey());
      // sp.addRequest(rq);

      Request rq1 = new Request(videoInfo + urlEntry.getKey());
      rq1.putExtra("fileName", Identities.uuid2() + ".json");
      rq1.putExtra("folder", urlEntry.getKey());
      sp.addRequest(rq1);
    }

    Pipeline pipeline = ctx.getBean("questionImgPipeline", Pipeline.class);
    sp.addPipeline(pipeline).thread(20).run();
  }

  private static Map<String, JSONObject> parseUrl() {
    File imgFile = new File("E:\\qres\\video\\qingke\\qing-list.json");
    Map<String, JSONObject> urlMap = new HashMap<>(300);
    StringBuilder sql = new StringBuilder("INSERT INTO `res_qing_course` (`id`,  `name`, `snapshot`) VALUES ");
    try {
      JSONObject json = JSON.parseObject(FileUtils.readFileToString(imgFile));
      JSONArray list = json.getJSONObject("data").getJSONArray("list");
      for (int i = 0; i < list.size(); i++) {
        JSONObject obj = list.getJSONObject(i);
        String id = obj.getString("id");
        String snapshot = obj.getString("snapshot");
        String newurl = talCdnHost + FileUtils.getFile(snapshot).getName();
        urlMap.put(id, obj);
        sql.append("('").append(id).append("',");
        sql.append("'").append(obj.getString("name")).append("',");
        sql.append("'").append(newurl).append("')");
        if (i < list.size() - 1) {
          sql.append(",");
        }
      }
      sql.append(";");
      File sqlFile = new File("E:\\qres\\video\\qingke\\qing-course.sql");
      if (!sqlFile.exists()) {
        FileUtils.write(sqlFile, sql);
      }
    } catch (Exception e) {
      logger.error("", e);
    }

    return urlMap;
  }
}
