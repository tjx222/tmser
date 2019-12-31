
package com.tmser.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tmser.dic.bo.WriteWay;
import com.tmser.dic.dao.WriteWayDao;

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
@Component("writeWayDbPipeline")
public class WriteWayDbPipeline implements Pipeline {

  private final static Logger logger = LoggerFactory.getLogger(WriteWayDbPipeline.class);

  @Autowired
  private WriteWayDao writeWayDao;

  /**
   * @param resultItems
   * @param task
   * @see us.codecraft.webmagic.pipeline.Pipeline#process(us.codecraft.webmagic.ResultItems, us.codecraft.webmagic.Task)
   */
  @Override
  public void process(ResultItems resultItems, Task task) {
    WriteWay writeWay = resultItems.get("writeWay");
    try {
      if (writeWay != null)
        writeWayDao.insert(writeWay);
    } catch (Exception e) {
      logger.warn("save voice failed [wordId='{}']", writeWay.getWordId());
      logger.warn("", e);
    }
  }

}
