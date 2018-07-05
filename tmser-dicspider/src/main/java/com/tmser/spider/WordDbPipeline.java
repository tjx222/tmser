/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.spider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.tmser.dic.bo.Pinyin;
import com.tmser.dic.bo.Pronunciation;
import com.tmser.dic.bo.Radical;
import com.tmser.dic.bo.Word;
import com.tmser.dic.dao.PinyinDao;
import com.tmser.dic.dao.PronunciationDao;
import com.tmser.dic.dao.RadicalDao;
import com.tmser.dic.dao.WordDao;

/**
 * <pre>
 * 详细字库数据存储到数据库
 * </pre>
 *
 * @author tjx1222
 * @version $Id: DicDbPipeline.java, v 1.0 2017年1月24日 下午10:41:23 tjx1222 Exp $
 */
@Component("wordPipeline")
public class WordDbPipeline implements Pipeline{
	
	private final static Logger logger = LoggerFactory.getLogger(WordDbPipeline.class);
	
	private static final Pattern bishunPtn = Pattern.compile("笔顺编号：(\\d+)");
	
	private static final Pattern descPtn = Pattern.compile("基本解释.*\\n*\\s*<hr class=\"hr1\">"
			+ "\\n*(.*)\\n*<br>\\s*<br>\\s*<b><span class=.*详细解释.*\\n*\\s*<hr class=\"hr1\">"
			+ "\\n*(.*)\\n*<br>\\s*<br>\\s*<b><span class=");
	@Autowired
	private WordDao wordDao;
	
	@Autowired
	private PronunciationDao pronunciationDao;
	

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
		String pinyin = resultItems.get("拼音：");
		String bihua = resultItems.get("笔划：");
		String bushou = resultItems.get("部首：");
		String wubi = resultItems.get("五笔：");
		String content = resultItems.get("content");
		String word = resultItems.get("word");
		if(word == null || pinyin == null || bushou == null){
			return;
		}else{
			pinyin = pinyin.trim();
			if(bushou.contains(",")){
				bushou = bushou.split(",")[0];
			}
			bushou = bushou.trim();
			word = word.trim();
		}
		
		try{
			Word wd = new Word();
			wd.setWord(word);
			Word oldwd = wordDao.getOne(wd);
			wd.setBiHuaShu(Integer.valueOf(bihua));
			wd.setWuBi(wubi);
			Radical rmodel = new Radical();
			rmodel.setContent(bushou);
			rmodel = radicalDao.getOne(rmodel);
			if(rmodel != null){
				wd.setRadical(rmodel.getContent());
				wd.setRadicalId(rmodel.getId());
			}
			
			String[] pinyins = pinyin.split(",");
			wd.setBiShun(parseBishun(content));
			fillDesc(content,wd);
			
			if(pinyins.length == 1 && pinyins[0].trim().contains(" ")){
					pinyins = pinyins[0].split(" ");
			}
			
			wd.setIsDuoYinZi(pinyins.length > 1);
			if(oldwd == null){
				wd = wordDao.insert(wd);
			}else{
				wd = oldwd;
			}
			
			for(String py : pinyins){
				py = py.trim();
				Pronunciation pro = new Pronunciation();
				String mpy = parseMPinyin(py,pro);
				Pinyin pmodel = new Pinyin();
				pmodel.setPinyin(mpy);
				pmodel = pinyinDao.getOne(pmodel);
				pro.setWord(wd.getWord());
				pro.setWordId(wd.getId());
				if(pmodel != null){
					pro.setDuYin(py);
					pro.setPinYin(mpy);
					pro.setPinYinId(pmodel.getId());
					pro.setShouZiMu(pmodel.getZimu());
					
					pronunciationDao.insert(pro);
				}
				
			}
			
		}catch(Exception e){
				logger.error("save word failed [word='{}']",word);
				logger.error("",e);
		}
		
	}
	
	private String parseBishun(String content){
		Matcher matcher = bishunPtn.matcher(content);
		if(matcher.find()){
			return matcher.group(1);
		}
		return null;
	}
	
	private void fillDesc(String content,Word wd){
		Matcher matcher = descPtn.matcher(content);
		if(matcher.find()){
			wd.setBasicDesc(matcher.group(1));
			wd.setDetailDesc(matcher.group(2));
		}
	}
	
	/**
	 * 解析拼音，无声调
	 * @param pinyin
	 * @return
	 */
	private static String parseMPinyin(String pinyin,Pronunciation pro){
		Matcher matcher = Constants.YUANYIN_PTN.matcher(pinyin);
		StringBuffer sb = new StringBuffer();
		String columnName = "";
		int sd = 0;
		while(matcher.find()) {
			String old = matcher.group();
			Integer nsd = Constants.PINYIN_SHENGDIAO_MAP.get(old);
			if(nsd > sd){
				sd = nsd;
			}
			columnName = Constants.YUANYIN_PINYIN_MAP.get(old);
			matcher.appendReplacement(sb, columnName); 
	    }
		matcher.appendTail(sb);
		pro.setShengDiao(sd);
		return sb.toString();
	}
	
}
