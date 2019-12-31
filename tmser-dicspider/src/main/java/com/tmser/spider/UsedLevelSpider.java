
package com.tmser.spider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
public class UsedLevelSpider {

  static class UsedLevelProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("UTF-8").setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
      List<Selectable> nodes = page.getHtml().xpath("//a[starts-with('@href','http://hanyu.iciba.com/hy/']").nodes();
      Map<Integer, List<String>> wordmaps = new HashMap<>();
      List<String> words = null;
      Integer level = 1;
      for (Selectable a : nodes) {
        String word = a.xpath("/a/text()").get().trim();
        words = wordmaps.get(level);
        if (words == null) {
          words = new ArrayList<String>();
          wordmaps.put(level, words);
        }
        words.add(word);
        if ("罐".equals(word)) {
          level = 2;
        }
      }
      page.putField(Constants.USED_LEVEL_MAP, wordmaps);
    }

    @Override
    public Site getSite() {
      return site;
    }
  }

  public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    Pipeline pipeline = ctx.getBean("usedLevelPipeline", Pipeline.class);
    Spider.create(new UsedLevelProcessor()).addPipeline(new ConsolePipeline()).addPipeline(pipeline)
        .addUrl("http://hanyu.iciba.com/zt/3500.html")

        .thread(5).run();
  }

}
