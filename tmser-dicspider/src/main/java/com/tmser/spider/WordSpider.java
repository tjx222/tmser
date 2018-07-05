/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.spider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import com.tmser.dic.bo.Pinyin;
import com.tmser.dic.bo.Radical;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tjx1222
 * @version $Id: DicSpider.java, v 1.0 2017年1月23日 下午10:49:58 tjx1222 Exp $
 */
public class WordSpider {
	
	static class WordPageProcessor implements PageProcessor{
		private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(100);

	    @Override
	    public void process(Page page) {
	    	if(page.getUrl().regex("bs\\.html").match()){
	    		List<Selectable> nodes = page.getHtml()
	    				.xpath("//table[@width='768']//tr").nodes();
	    		List<Radical> rds = new ArrayList<Radical>();
	    		List<Pinyin> pinyins = new ArrayList<Pinyin>();
	    		int bihua = 1;
	    		for(Selectable tr : nodes){
	    			List<Selectable> bushou = tr.$("a.fontbox").nodes();
	    			for(Selectable as : bushou ){
	    				Radical rd = new Radical();
	    				rd.setBihuashu(bihua);
	    				rd.setContent(as.xpath("//a/text()").get().trim());
	    				page.addTargetRequest(as.links().get());
	    				System.out.println("-----" + as.get() + " -亅--"+rd.getContent());
	    			}
	    		}
	    		page.putField(Constants.RADICAL_LIST, rds);
	    		page.putField(Constants.PINYIN_LIST, pinyins);
	    	}else if(page.getUrl().regex("html/\\d*\\.html").match()){
	    		//部首二级页面
	    		List<Selectable> nodes = page.getHtml()
	    				.$("a.fontbox").nodes();
	    		for(Selectable as : nodes ){
	    			page.addTargetRequest(as.links().get());
	    		}
	    	}else {//字详情页
	    		List<Selectable> nodes = page.getHtml()
	    				.xpath("//table[@width='95%']//tr").nodes();
	    		page.putField("word", page.getHtml().xpath("//td[@class='font_22']/text()").get());
	    		for(Selectable tr : nodes){
	    			page.putField(tr.xpath("/tr/td[1]/b/text()").get(), tr.xpath("/tr/td[2]/text()").get());
	    			page.putField(tr.xpath("/tr/td[3]/b/text()").get(), tr.xpath("/tr/td[4]/text()").get());
	    		}
	    		page.putField("content", page.getHtml().xpath("//td[@class='font_18']").get());
	    	}
	    }

	    @Override
	    public Site getSite() {
	        return site;
	    }
	}
	
	public static void main(String[] args) {
		 ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/config/spring/*.xml");
		 Pipeline pipeline = ctx.getBean("wordPipeline",Pipeline.class);
		 Spider.create(new WordPageProcessor())
		 .addPipeline(pipeline)
		 .addUrl("http://xh.5156edu.com/html3/25617.html")
		 .thread(5).run();
	}

}
