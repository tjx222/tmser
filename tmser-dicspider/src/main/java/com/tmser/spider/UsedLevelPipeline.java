/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.spider;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.tmser.dic.bo.Word;
import com.tmser.dic.dao.WordDao;

/**
 * <pre>
 * 数据存储到数据库
 * </pre>
 *
 * @author tjx1222
 * @version $Id: DicDbPipeline.java, v 1.0 2017年1月24日 下午10:41:23 tjx1222 Exp $
 */
@Component("usedLevelPipeline")
public class UsedLevelPipeline implements Pipeline{
	
	private final static Logger logger = LoggerFactory.getLogger(UsedLevelPipeline.class);
	
	@Autowired
	private WordDao wordDao;
	
	/**
	 * @param resultItems
	 * @param task
	 * @see us.codecraft.webmagic.pipeline.Pipeline#process(us.codecraft.webmagic.ResultItems, us.codecraft.webmagic.Task)
	 */
	@Override
	public void process(ResultItems resultItems, Task task) {
		 Map<Integer,List<String>> wordmaps = resultItems.get(Constants.USED_LEVEL_MAP);
		for(Integer level : wordmaps.keySet()){
			List<String> words = wordmaps.get(level);
			try{
				for (String word : words) {
					Word wd = new Word();
					wd.setWord(word);
					wd.addCustomCulomn("id");
					wd = wordDao.getOne(wd);
					if(wd != null){
						Word model = new Word();
						model.setId(wd.getId());
						model.setUsedLevel(level);
						wordDao.update(model);
					}else{
						System.out.println("-----" + level+ "级常用字不存在："+word);
					}
				}
				
			}catch(Exception e){
				logger.warn("save word user level faild");
				logger.warn("",e);
			}
		}
	}

}
