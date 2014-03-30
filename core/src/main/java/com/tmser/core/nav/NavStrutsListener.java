package com.tmser.core.nav;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ClassPathFinder;
import com.tmser.core.config.Constants;

/**
 * 导航配置加载
 * @author tjx
 * @version 2.0
 * 2014-01-07
 */
@Component
@Lazy(false)
public class NavStrutsListener implements DispatcherListener{
	private static final Logger log = LoggerFactory.getLogger(NavStrutsListener.class);
	private String navConfig;
	
	private NavHolder navHolder;
	
/*  private ServletContext ctx;
	@Inject
	public void setCtx(ServletContext ctx) {
		this.ctx = ctx;
	}*/

	private final static String DEFAULT_NAV_CONFIG = "config/nav/nav-*.xml";
	
	public NavStrutsListener(){
		Dispatcher.addDispatcherListener(this);
	}
	
    @Inject(value=Constants.TSP_NAV_CONFIG,required=false)
    public void setNavConfig(String navConfig) {
    	this.navConfig = navConfig;
    }
    
	@Override
	public void dispatcherDestroyed(Dispatcher du) {
		this.navHolder = null;
	}

	@Override
	public void dispatcherInitialized(Dispatcher du) {
		du.getContainer().inject(this);
		if (navConfig == null) {
			navConfig = DEFAULT_NAV_CONFIG;
			log.debug("use the default nav config location:"+navConfig);
        }
		//if(ctx != null){
			 ClassPathFinder wildcardFinder = new ClassPathFinder();
             wildcardFinder.setPattern(navConfig);
             Vector<String> wildcardMatches = wildcardFinder.findMatches();
             // Parse the configuration for this module
             if(navHolder != null)
             {
            	 return ;
             }
             NavHolder.clear();
             navHolder = new NavHolder();
             // Configure the Digester instance we will use
             NavXmlParser parser = new NavXmlParser();

             for (String match : wildcardMatches) {
            	 InputStream input = null;
                 try {
                     URL url = ClassLoaderUtil.getResource(match,getClass());
                     InputSource is = new InputSource(url.toExternalForm());
                     input = ClassLoaderUtil.getResourceAsStream(match,getClass());
                     is.setByteStream(input);
                     parser.parse(is, navHolder);
                     log.info("loaded nav config file: "+match+",total nav size:"+NavHolder.size());
                 }catch (MalformedURLException e) {
                     log.error("Nav path not exist:"+match, e);
                 } catch (SAXException e) {
                 	log.error(match + "is not a illegal xml file! ", e);
         		} catch (IOException e) {
         			log.error(match + " loaded failed", e);
         		}finally {
                     if (input != null) {
                         try {
                             input.close();
                         } catch (IOException e) {
                        	 log.error("Unable to close input stream", e);
                         }
                     }
                 }
             }
             }
		//}
}
