
package com.tmser.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tmser.dic.bo.Voice;
import com.tmser.dic.dao.VoiceDao;

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
@Component("voiceDbPipeline")
public class VoiceDbPipeline implements Pipeline {

  private final static Logger logger = LoggerFactory.getLogger(VoiceDbPipeline.class);

  @Autowired
  private VoiceDao voiceDao;

  /**
   * @param resultItems
   * @param task
   * @see us.codecraft.webmagic.pipeline.Pipeline#process(us.codecraft.webmagic.ResultItems, us.codecraft.webmagic.Task)
   */
  @Override
  public void process(ResultItems resultItems, Task task) {
    Voice voice = resultItems.get("voice");
    try {
      if (voice != null)
        voiceDao.insert(voice);
    } catch (Exception e) {
      logger.warn("save voice failed [content='{}']", voice.getPinyinId());
      logger.warn("", e);
    }
  }

}
