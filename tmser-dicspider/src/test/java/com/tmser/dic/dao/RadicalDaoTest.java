package com.tmser.dic.dao;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath*:/config/spring/*.xml")
public class RadicalDaoTest {
	Logger x = LoggerFactory.getLogger("x");
	Logger xy = LoggerFactory.getLogger("x.y");
	Logger xyz = LoggerFactory.getLogger("x.y.z");
	
	@Autowired
	private RadicalDao radicalDao;
	
	//@Test
	public void testEnv(){
		Assert.assertNotNull(radicalDao);
	}
	
	@Test
	public void testLogger(){
		xyz.error("----------------xyz");
	}
}
