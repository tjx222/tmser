
package com.tmser.spider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tmser.dic.bo.Pinyin;
import com.tmser.dic.bo.Radical;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * <pre>
 *  爬取新华字典 字，拼音，解释等信息
 * </pre>
 *
 * @author tjx1222
 * @version $Id: DicSpider.java, v 1.0 2017年1月23日 下午10:49:58 tjx1222 Exp $
 */
public class DicSpider {

  static class DicPageProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
      List<Selectable> nodes = page.getHtml().xpath("//table[@width='768']//tr").nodes();
      List<Radical> rds = new ArrayList<Radical>();
      List<Pinyin> pinyins = new ArrayList<Pinyin>();
      int bihua = 1;
      for (Selectable tr : nodes) {
        String bihuashu = tr.xpath("//*[@class='font_14']/text()").get().trim();
        List<Selectable> bushou = tr.$("a.fontbox").nodes();
        for (Selectable as : bushou) {
          if (page.getUrl().regex("pinyi\\.html").match()) {
            System.out.println(bihuashu + "------" + as.get());
            Pinyin rd = new Pinyin();
            rd.setZimu(bihuashu.trim());
            rd.setPinyin(as.xpath("//a/text()").get().trim());
            pinyins.add(rd);

          } else {
            Radical rd = new Radical();
            rd.setBihuashu(bihua);
            rd.setContent(as.xpath("//a/text()").get().trim());
            rds.add(rd);
            System.out.println("----ü--" + as.get() + " -亅--" + rd.getContent());
          }

        }
      }
      page.putField(Constants.RADICAL_LIST, rds);
      page.putField(Constants.PINYIN_LIST, pinyins);
    }

    @Override
    public Site getSite() {
      return site;
    }
  }

  public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    Pipeline pipeline = ctx.getBean("dicDbPipeline", Pipeline.class);
    Spider.create(new DicPageProcessor()).addPipeline(new ConsolePipeline()).addPipeline(pipeline)
        .addUrl("http://xh.5156edu.com/bs.html").thread(5).run();
  }

}
