
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
public class VideoTsSpider2 {
  private static final Logger logger = LoggerFactory.getLogger(VideoTsSpider2.class);
  private static String m3u8CdnHost = "http://pic.test.com";
  private static String jsonHost = "http://www.test.com/v2f/resource/browse-detail?id=";
  private static String talCdnHost = "http://cdn.test.com/cdn/qingcourse/";
  static AtomicInteger counter = new AtomicInteger(0);
  static AtomicInteger failed = new AtomicInteger(0);
  static AtomicInteger total = new AtomicInteger(0);

  static StringBuilder sql = new StringBuilder(
      "INSERT INTO `res_qing_video` (`name`, `video`, `snapshot`,`view_time`,`course_id`,`type`) VALUES ");

  static File magicFolder = new File("E:\\qres\\video\\magic"); // 模方
  static File brainFolder = new File("E:\\qres\\video\\brain"); // 脑科学
  static File weiqiFolder = new File("E:\\qres\\video\\weiqi"); // 脑科学
  static File abilityFolder = new File("E:\\qres\\video\\ability"); // 七大能力

  static File currentFolder = weiqiFolder;

  static class VideoTsSpider2Processor implements PageProcessor {
    private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setTimeOut(30000).setSleepTime(100);

    @Override
    public void process(Page page) {
      counter.incrementAndGet();
      if (page.getStatusCode() == 200) {
        String fileName = (String) page.getRequest().getExtra("fileName");
        if (fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("png")
            || fileName.endsWith("ts")) {
          saveFileContent(page, fileName);
        } else if (fileName.endsWith("m3u8")) {
          saveM3u8TsFile(page, fileName);
        } else if (fileName.endsWith("json")) {
          parseJsonContent(page, fileName);
        } else {
          logger.info("no process for file, filename:{}", fileName);
        }

      } else {
        logger.info("download failed count: {}, filename:{}", failed.addAndGet(1),
            page.getRequest().getExtra("fileName"));
      }

    }

    private void parseJsonContent(Page page, String fileName) {
      byte[] content = page.getRawContent();
      JSONObject obj = JSON.parseObject(new String(content)).getJSONObject("data");
      addVideos(page, obj, FileUtils.getFileNameNoExtension(fileName), 1);
    }

    private static void addVideos(Page page, JSONObject obj, String courseId, int type) {
      String snapshot = obj.getString("snapshot");
      String video = obj.getString("video");
      String viewTime = obj.getString("view_time");
      String name = obj.getString("name");

      String fileName = FileUtils.getFile(snapshot).getName();
      String newurl = talCdnHost + currentFolder.getName() + "/img/" + fileName;
      File ts = new File(currentFolder, "img/" + fileName);
      if (!ts.exists()) {
        Request rq = new Request(snapshot);
        rq.putExtra("fileName", "img/" + fileName);
        page.addTargetRequest(rq);
        total.incrementAndGet();
      }

      String m3u8Name = FileUtils.getFile(video).getName();
      String videoUrl = talCdnHost + currentFolder.getName() + "/video/" + m3u8Name;
      ts = new File(currentFolder, "video/" + m3u8Name);
      if (!ts.exists()) {
        Request rq1 = new Request(video);
        rq1.putExtra("fileName", "video/" + m3u8Name);
        page.addTargetRequest(rq1);
        total.incrementAndGet();
      } else {
        logger.info("---- file {} exists", fileName);
      }

      sql.append("('").append(name).append("',");
      sql.append("'").append(videoUrl).append("',");
      sql.append("'").append(newurl).append("',");
      sql.append("'").append(viewTime).append("',");
      sql.append("'").append(courseId).append("',");
      sql.append("'").append(type).append("'),\r\n");
    }

    private void saveM3u8TsFile(Page page, String fileName) {
      File m3u8File = saveFileContent(page, fileName);
      if (m3u8File == null) {
        return;
      }
      File fileFolder = currentFolder;
      try {
        for (String tsLine : FileUtils.readLines(m3u8File)) {
          if (tsLine.trim().startsWith("/")) {
            String videoUrl = m3u8CdnHost + tsLine;
            String m3u8Name = tsLine;

            File ts = new File(fileFolder, m3u8Name);
            if (!ts.exists()) {
              Request rq = new Request(videoUrl);
              rq.putExtra("fileName", "video" + m3u8Name);
              page.addTargetRequest(rq);
              total.incrementAndGet();
            }
          }
        }
      } catch (IOException e) {
        logger.error("", e);
      }

    }

    private File saveFileContent(Page page, String fileName) {
      File f = new File(currentFolder, fileName);

      if (!f.exists()) {
        byte[] content = page.getRawContent();
        try {
          FileUtils.forceMkdir(f.getParentFile());
          FileUtils.writeByteArrayToFile(f, content);
          int c = counter.get();
          logger.info("total: {} ,aready download:{}, remain:{}, fileName:{}", total, c, total.get() - c, fileName);
        } catch (IOException e) {
          logger.error("", e);
        }
      } else {
        return null;
      }

      return f;

    }

    @Override
    public Site getSite() {
      return site;
    }

  }

  public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    Map<String, JSONObject> urls = parseJsonUrl();
    Spider sp = Spider.create(new VideoTsSpider2Processor());
    for (Entry<String, JSONObject> urlEntry : urls.entrySet()) {
      JSONObject val = urlEntry.getValue();
      Request rq = new Request(urlEntry.getKey());
      rq.putExtra("fileName", val.getString("name"));
      sp.addRequest(rq);

      String id = val.getString("id");
      Request jsonq = new Request(jsonHost + id);
      jsonq.putExtra("fileName", id + ".json");
      sp.addRequest(jsonq);
      total.addAndGet(2);
    }

    Pipeline pipeline = ctx.getBean("questionImgPipeline", Pipeline.class);
    sp.addPipeline(pipeline).thread(20).run();
    String type = currentFolder.getName();
    File updatesqlFile = new File(currentFolder, type + "_video.sql");
    try {
      if (!updatesqlFile.exists()) {
        FileUtils.write(updatesqlFile, sql);
      }
    } catch (IOException e) {
      logger.error("", e);
    }
  }

  private static Map<String, JSONObject> parseJsonUrl() {
    File fileFolder = currentFolder;
    Map<String, JSONObject> urlMap = new HashMap<>(300);
    String type = fileFolder.getName();
    StringBuilder sql = new StringBuilder("INTO `res_qing_course` (`id`,  `name`, `snapshot`, `type`) VALUES ");
    for (File jsonFile : fileFolder
        .listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".json"))) {
      try {
        JSONObject json = JSON.parseObject(FileUtils.readFileToString(jsonFile));

        JSONArray courses = json.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < courses.size(); i++) {
          JSONObject obj = courses.getJSONObject(i);
          String id = obj.getString("id");
          String snapshot = obj.getString("snapshot");
          String name = FileUtils.getFile(snapshot).getName();
          String newurl = talCdnHost + currentFolder.getName() + "/img/" + name;
          sql.append("('").append(id).append("',");
          sql.append("'").append(obj.getString("name")).append("',");
          sql.append("'").append(newurl).append("',");
          sql.append("'").append(type).append("')");

          obj.put("name", "img/" + name);
          urlMap.put(snapshot, obj);

          if (i < courses.size() - 1) {
            sql.append(",");
          }
        }
      } catch (IOException e) {
        logger.error("", e);
      }
    }
    sql.append(";");

    File updatesqlFile = new File(fileFolder, type + "_course.sql");

    try {
      if (!updatesqlFile.exists()) {
        FileUtils.write(updatesqlFile, sql);
      }
    } catch (IOException e) {
      logger.error("", e);
    }
    return urlMap;
  }

}
