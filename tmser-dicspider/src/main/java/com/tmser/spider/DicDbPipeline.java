
package com.tmser.spider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tmser.dic.bo.Pinyin;
import com.tmser.dic.bo.Radical;
import com.tmser.dic.dao.PinyinDao;
import com.tmser.dic.dao.RadicalDao;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * <pre>
 * 数据存储到数据库
 * </pre>
 *
 * @author tjx1222
 * @version $Id: DicDbPipeline.java, v 1.0 2017年1月24日 下午10:41:23 tjx1222 Exp $
 */
@Component("dicDbPipeline")
public class DicDbPipeline implements Pipeline {

  private final static Logger logger = LoggerFactory.getLogger(DicDbPipeline.class);

  @Autowired
  private RadicalDao radicalDao;

  @Autowired
  private PinyinDao pinyinDao;

  /**
   * @param resultItems
   * @param task
   * @see us.codecraft.webmagic.pipeline.Pipeline#process(us.codecraft.webmagic.ResultItems, us.codecraft.webmagic.Task)
   */
  @Override
  public void process(ResultItems resultItems, Task task) {
    List<Radical> rdList = resultItems.get(Constants.RADICAL_LIST);
    for (Radical rd : rdList) {
      try {
        radicalDao.insert(rd);
      } catch (Exception e) {
        logger.warn("save radical failed [content='{}']", rd.getContent());
        logger.warn("", e);
      }
    }

    List<Pinyin> pinyins = resultItems.get(Constants.PINYIN_LIST);
    for (Pinyin rd : pinyins) {
      try {
        pinyinDao.insert(rd);
      } catch (Exception e) {
        logger.warn("save pinyin failed [content='{}']", rd.getPinyin());
        logger.warn("", e);
      }
    }
  }

}
