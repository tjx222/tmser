
package com.tmser.question;

import org.springframework.stereotype.Component;

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
@Component("questionImgPipeline")
public class QuestionImgPipeline implements Pipeline {

  /**
   * @param resultItems
   * @param task
   * @see us.codecraft.webmagic.pipeline.Pipeline#process(us.codecraft.webmagic.ResultItems, us.codecraft.webmagic.Task)
   */
  @Override
  public void process(ResultItems resultItems, Task task) {
  }

}
