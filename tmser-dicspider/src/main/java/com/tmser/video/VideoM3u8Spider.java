
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
public class VideoM3u8Spider {
  private final static Logger logger = LoggerFactory.getLogger(VideoM3u8Spider.class);
  static String videoInfo = "http://www.test.com/v2f/resource/qing-detail-new?username=13821540799&id=";
  private static String talCdnHost = "http://cdn.test.com/cdn/qingcourse/img/";
  private static String m3u8CdnHost = "http://cdn.test.com/cdn/qingcourse/v";

  static class VideoM3u8SpiderProcessor implements PageProcessor {
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
    Spider sp = Spider.create(new VideoM3u8SpiderProcessor());
    for (Entry<String, JSONObject> urlEntry : urls.entrySet()) {
      JSONObject val = urlEntry.getValue();
      Request rq = new Request(urlEntry.getKey());
      rq.putExtra("fileName", val.getString("name"));
      rq.putExtra("folder", val.getString("folder"));
      sp.addRequest(rq);
    }

    Pipeline pipeline = ctx.getBean("questionImgPipeline", Pipeline.class);
    sp.addPipeline(pipeline).thread(20).run();
  }

  private static Map<String, JSONObject> parseUrl() {
    File fileFolder = new File("E:\\qres\\video\\qingke");
    Map<String, JSONObject> urlMap = new HashMap<>(300);
    StringBuilder updatesql = new StringBuilder();

    StringBuilder lecturesql = new StringBuilder(
        "INSERT INTO `res_qing_lecture` (`id`, `name`,`course_id`,`num`) VALUES ");

    StringBuilder videosql = new StringBuilder(
        "INSERT INTO `res_qing_video` (`name`, `video`, `snapshot`,`view_time`,`course_id`,`lecture_id`,type`) VALUES ");
    int lectureid = 1;

    for (File f : fileFolder.listFiles(file -> file.isDirectory())) {
      String courseId = f.getName();
      for (File jsonFile : f.listFiles(file -> file.isFile() && file.getName().endsWith(".json"))) {
        try {
          JSONObject json = JSON.parseObject(FileUtils.readFileToString(jsonFile));
          JSONObject course = json.getJSONObject("data");

          String grade = course.getString("grade");
          String subject = course.getString("subject");

          updatesql
              .append(String.format("update `res_qing_course` set  `grade` = '%s', `subject` = '%s' where `id`='%s';",
                  grade, subject, courseId))
              .append("\r\n");

          JSONObject list = course.getJSONObject("arr");
          for (Entry<String, Object> entry : list.entrySet()) {
            JSONObject obj = (JSONObject) JSON.toJSON(entry.getValue());

            String lectureId = String.valueOf(lectureid);
            String lectureTimes = obj.getString("lecture_times");

            JSONArray qlist = obj.getJSONArray("qlist");
            JSONArray resList = obj.getJSONArray("resList");

            addVideos(qlist, urlMap, videosql, courseId, lectureId, 1);
            addVideos(resList, urlMap, videosql, courseId, lectureId, 2);
            lectureid++;

            lecturesql.append("('").append(lectureId).append("',");
            lecturesql.append("'").append(lectureTimes).append("',");
            lecturesql.append("'").append(courseId).append("',");
            lecturesql.append("'").append(entry.getKey()).append("'),");
          }

          lecturesql.append("\r\n");
        } catch (Exception e) {
          logger.error("", e);
        }

      }
    }

    lecturesql.append(";");
    videosql.append(";");
    File lecturesqlFile = new File("E:\\qres\\video\\qingke\\lecture.sql");
    File videosqlFile = new File("E:\\qres\\video\\qingke\\video.sql");
    File updatesqlFile = new File("E:\\qres\\video\\qingke\\course_update.sql");

    try {
      if (!lecturesqlFile.exists()) {
        FileUtils.write(lecturesqlFile, lecturesql);
      }
      if (!videosqlFile.exists()) {
        FileUtils.write(videosqlFile, videosql);
      }
      if (!updatesqlFile.exists()) {
        FileUtils.write(updatesqlFile, updatesql);
      }
    } catch (IOException e) {
      logger.error("", e);
    }
    return urlMap;

  }

  private static void addVideos(JSONArray qlist, Map<String, JSONObject> urlMap, StringBuilder sql, String courseId,
      String lectureId, int type) {

    for (int i = 0; i < qlist.size(); i++) {
      JSONObject obj = qlist.getJSONObject(i);
      String snapshot = obj.getString("snapshot");
      String video = obj.getString("video");
      String viewTime = obj.getString("view_time");
      String name = obj.getString("name");

      String newurl;
      if (urlMap.containsKey(snapshot)) {
        logger.info("has dupcate key , {} - {}", courseId, snapshot);
        newurl = urlMap.get(snapshot).getString("url");
      } else {
        String fileName = FileUtils.getFile(snapshot).getName();
        newurl = talCdnHost + fileName;
        JSONObject info = new JSONObject();
        info.put("folder", "img");
        info.put("name", fileName);
        info.put("url", newurl);
        urlMap.put(snapshot, info);
      }

      String videoUrl;
      if (urlMap.containsKey(video)) {
        logger.info("has dupcate key , {} - {}", courseId, video);
        videoUrl = urlMap.get(video).getString("url");
      } else {
        String folder = "/" + courseId + "/" + lectureId;
        String m3u8Name = FileUtils.getFile(video).getName();
        videoUrl = m3u8CdnHost + folder + "/" + m3u8Name;
        JSONObject vinfo = new JSONObject();
        vinfo.put("folder", folder);
        vinfo.put("name", m3u8Name);
        vinfo.put("url", videoUrl);
        urlMap.put(video, vinfo);
      }

      sql.append("('").append(name).append("',");
      sql.append("'").append(videoUrl).append("',");
      sql.append("'").append(newurl).append("',");
      sql.append("'").append(viewTime).append("',");
      sql.append("'").append(courseId).append("',");
      sql.append("'").append(lectureId).append("',");
      sql.append("'").append(type).append("'),");

      if (i < qlist.size() - 1) {
        sql.append("\r\n");
      }
    }
  }

}
