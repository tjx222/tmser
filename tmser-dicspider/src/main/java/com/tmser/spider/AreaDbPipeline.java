
package com.tmser.spider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tmser.dic.bo.Area;
import com.tmser.dic.dao.AreaDao;

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
@Component("areaDbPipeline")
public class AreaDbPipeline implements Pipeline {

  private final static Logger logger = LoggerFactory.getLogger(AreaDbPipeline.class);

  @Autowired
  private AreaDao areaDao;

  /**
   * @param resultItems
   * @param task
   * @see us.codecraft.webmagic.pipeline.Pipeline#process(us.codecraft.webmagic.ResultItems, us.codecraft.webmagic.Task)
   */
  @Override
  public void process(ResultItems resultItems, Task task) {
    List<Area> areaList = resultItems.get(Constants.RADICAL_LIST);
    try {
      areaDao.batchInsert(areaList);
    } catch (Exception e) {
      logger.warn("", e);
    }
  }

}
