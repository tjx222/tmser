
package com.tmser.spider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tmser.dic.bo.Pinyin;
import com.tmser.dic.bo.Voice;
import com.tmser.dic.dao.PinyinDao;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 * 查询所有拼音获取拼音读法
 * </pre>
 *
 * @author tjx1222
 * @version $Id: DicSpider.java, v 1.0 2017年1月23日 下午10:49:58 tjx1222 Exp $
 */
public class VoiceSpider {

  private final static Logger logger = LoggerFactory.getLogger(VoiceSpider.class);

  static class VoiceProcessor implements PageProcessor {
    private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
      if (page.getStatusCode() == 200) {
        Voice v = new Voice();
        v.setPinyinId((Integer) page.getRequest().getExtra("pinyinId"));
        v.setShengdiao((Integer) page.getRequest().getExtra("shengdiao"));
        v.setVoice(page.getRawContent());
        page.putField("voice", v);
      } else {
        logger.error("can't found mp3 ,pinyinid: " + page.getRequest().getExtra("pinyinId") + ",shengdiao :"
            + page.getRequest().getExtra("shengdiao"));
      }

    }

    @Override
    public Site getSite() {
      return site;
    }
  }

  public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
    PinyinDao pinyinDao = ctx.getBean(PinyinDao.class);
    List<Pinyin> pinyins = pinyinDao.listAll(new Pinyin());
    Spider sp = Spider.create(new VoiceProcessor());
    for (Pinyin py : pinyins) {
      for (int sd = 1; sd < 5; sd++) {
        Request rq = new Request("http://xh.5156edu.com/xhzdmp3abc/" + py.getPinyin() + sd + ".mp3");
        rq.putExtra("pinyinId", py.getId());
        rq.putExtra("shengdiao", sd);
        sp.addRequest(rq);
      }
    }

    Pipeline pipeline = ctx.getBean("voiceDbPipeline", Pipeline.class);
    sp.addPipeline(pipeline).thread(5).run();
  }

}
