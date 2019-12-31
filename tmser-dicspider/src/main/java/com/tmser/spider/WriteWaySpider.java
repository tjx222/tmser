
package com.tmser.spider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tmser.dic.bo.Word;
import com.tmser.dic.bo.WriteWay;
import com.tmser.dic.dao.WordDao;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * <pre>
 * 查询所有拼音获取拼音读法
 * </pre>
 *
 * @author tjx1222
 * @version $Id: DicSpider.java, v 1.0 2017年1月23日 下午10:49:58 tjx1222 Exp $
 */
public class WriteWaySpider {

  private final static Logger logger = LoggerFactory.getLogger(WriteWaySpider.class);

  static class WriteWayProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("UTF-8").setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
      if (page.getUrl().regex("/hy/").match()) {
        // http-equiv="refresh" content="0; url=/hanzi/7188.shtml"
        List<Selectable> nodes = page.getHtml().xpath("//meta[@http-equiv='refresh']").nodes();
        for (Selectable meta : nodes) {
          Request rq = new Request("http://hanyu.iciba.com/hanzi/" + meta.regex("\\d+\\.shtml").get());
          rq.putExtra("wordId", page.getRequest().getExtra("wordId"));
          rq.putExtra("ext", page.getRequest().getExtra("ext"));
          page.addTargetRequest(rq);
        }
      } else if (page.getUrl().regex("/hanzi/").match()) {
        List<Selectable> nodes = page.getHtml().xpath("//param[@name='movie']").nodes();
        for (Selectable param : nodes) {
          String path = param.xpath("/param/@value").get();
          if (path.startsWith("/hanzi.swf")) {
            Request rq = new Request("http://hanyu.iciba.com" + path);
            rq.putExtra("wordId", page.getRequest().getExtra("wordId"));
            rq.putExtra("ext", page.getRequest().getExtra("ext"));
            page.addTargetRequest(rq);
          }
        }
      } else {
        if (page.getStatusCode() == 200) {
          WriteWay v = new WriteWay();
          v.setWordId((Integer) page.getRequest().getExtra("wordId"));
          v.setExt((String) page.getRequest().getExtra("ext"));
          v.setData(page.getRawContent());
          v.setSize(v.getData().length);
          page.putField("writeWay", v);
        } else {
          logger.error("can't found mp3 ,pinyinid: " + page.getRequest().getExtra("pinyinId") + ",shengdiao :"
              + page.getRequest().getExtra("shengdiao"));
        }
      }
    }

    @Override
    public Site getSite() {
      return site;
    }
  }

  public static void main(String[] args) throws UnsupportedEncodingException {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    WordDao wordDao = ctx.getBean(WordDao.class);
    Word model = new Word();
    model.buildCondition(" and (usedLevel =:level1 or usedLevel = :level2 )").put("level1", 1).put("level2", 2);
    List<Word> words = wordDao.list(model, 3500);
    Spider sp = Spider.create(new WriteWayProcessor());
    for (Word word : words) {
      Request rq = new Request("http://hanyu.iciba.com/hy/" + URLEncoder.encode(word.getWord(), "utf-8") + "/");
      rq.putExtra("wordId", word.getId());
      rq.putExtra("ext", "swf");
      sp.addRequest(rq);
    }

    Pipeline pipeline = ctx.getBean("writeWayDbPipeline", Pipeline.class);
    sp.addPipeline(pipeline).thread(5).run();
  }

}
