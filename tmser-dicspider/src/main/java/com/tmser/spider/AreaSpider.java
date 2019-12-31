
package com.tmser.spider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tmser.dic.bo.Area;
import com.tmser.dic.dao.AreaDao;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 *  爬取新华字典 字，拼音，解释等信息
 * </pre>
 *
 * @author tjx1222
 * @version $Id: DicSpider.java, v 1.0 2017年1月23日 下午10:49:58 tjx1222 Exp $
 */
public class AreaSpider {

  static class AreaPageProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("UTF-8").setRetryTimes(3).setSleepTime(100);

    private AreaDao areaDao;

    /**
     * @param areaDao2
     */
    public AreaPageProcessor(AreaDao areaDao) {
      this.areaDao = areaDao;
    }

    public void setAreaDao(AreaDao areaDao) {
      this.areaDao = areaDao;
    }

    @Override
    public void process(Page page) {
      String jsonContent = page.getRawText();
      List<Area> rds = new ArrayList<Area>();
      JSONObject jc = JSONObject.parseObject(jsonContent);
      if (jc.getInteger("status") == 0) {
        JSONArray rs = jc.getJSONArray("result");
        int i = 0;
        for (Object obj : rs) {
          JSONObject area = (JSONObject) obj;
          // {"id":"2","name":"安徽","parentid":"0","parentname":"","areacode":"","zipcode":"","depth":"1"}
          Integer aid = area.getInteger("id");
          Integer level = area.getInteger("depth");
          Area old = areaDao.get(aid);
          if (old == null) {
            Area ar = new Area();
            ar.setCode(area.getString("areacode"));
            ar.setId(aid);
            ar.setLevel(level);
            ar.setParentId(area.getInteger("parentid"));
            ar.setName(area.getString("name"));
            ar.setPostcode(area.getString("zipcode"));
            ar.setSort(++i);
            rds.add(ar);
            if (aid != 51 && level < 3)
              page.addTargetRequest("https://jisuarea.market.alicloudapi.com/area/query?parentid=" + aid);
          } else {
            Area model = new Area();
            model.setParentId(aid);
            List<Area> allChild = areaDao.listAll(model);
            addreq(old, allChild, page);
          }

        }
      }
      page.putField(Constants.RADICAL_LIST, rds);
    }

    void addreq(Area p, List<Area> allChild, Page page) {
      if (allChild.size() > 0) {
        for (Area area2 : allChild) {
          Area model = new Area();
          model.setParentId(area2.getId());
          List<Area> children = areaDao.listAll(model);
          addreq(area2, children, page);
        }
      } else if (p.getId() != 51 && p.getLevel() < 3) {
        if (p.getParentId() == 1 || p.getParentId() == 32 || p.getParentId() == 34 || p.getParentId() == 33
            || p.getParentId() == 26 || p.getParentId() == 8 || p.getParentId() == 31) {
          return;
        }
        page.addTargetRequest("https://jisuarea.market.alicloudapi.com/area/query?parentid=" + p.getId());
      }
    }

    @Override
    public Site getSite() {
      return site;
    }
  }

  public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    HttpClientDownloader httpClientDownloader = new HttpClientDownloader() {
      @Override
      protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers,
          HttpHost proxy) {
        HttpUriRequest req = super.getHttpUriRequest(request, site, headers, proxy);
        req.addHeader("Authorization", "APPCODE f6d2f8f2332546679f3fe1fb0104966a");
        req.addHeader("Content-Type", "application/json; charset=utf-8");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        return req;
      }

    };
    Pipeline pipeline = ctx.getBean("areaDbPipeline", Pipeline.class);
    AreaDao areaDao = ctx.getBean(AreaDao.class);
    Spider.create(new AreaPageProcessor(areaDao)).addPipeline(new ConsolePipeline()).addPipeline(pipeline)
        .setDownloader(httpClientDownloader).addUrl("https://jisuarea.market.alicloudapi.com/area/query?parentid=0")
        .thread(1).run();
  }

}
