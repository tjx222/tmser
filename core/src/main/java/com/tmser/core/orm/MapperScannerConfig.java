package com.tmser.core.orm;

import static org.springframework.util.Assert.notNull;

import java.lang.annotation.Annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * bo 扫描配置
 * 由spring 管理，在bean属性设置完成后自动调用bo 扫描器进行bo 扫描
 * @author tjx
 * @version 2.0
 * 2014-1-16
 */
public class MapperScannerConfig  implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware{
	  private String basePackage;
	  private Class<? extends Annotation> annotationClass = javax.persistence.Entity.class;
	  private ApplicationContext applicationContext;

	  /**
	   * 设置扫描位置，即基包。
	   * <p>
	   * 可以使用半角逗号分割多个基包。
	   * <p>
	   * 扫描器将在设置的基包下扫描。
	   *
	   * @param basePackage 基包名称
	   */
	  public void setBasePackage(String basePackage) {
	    this.basePackage = basePackage;
	  }

	  /**
	   * {@inheritDoc}
	   */
	  public void setApplicationContext(ApplicationContext applicationContext) {
	    this.applicationContext = applicationContext;
	  }
	  
	  /**
	   * This property specifies the annotation that the scanner will search for.
	   * <p>
	   * The scanner will register all interfaces in the base package that also have the
	   * specified annotation.
	   * <p>
	   * Note this can be combined with markerInterface.
	   *
	   * @param annotationClass annotation class
	   */
	  public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
	    this.annotationClass = annotationClass;
	  }

	  /**
	   * {@inheritDoc}
	   */
	  public void afterPropertiesSet() throws Exception {
	    notNull(this.basePackage, "Property 'basePackage' is required");
	  }

	  /**
	   * {@inheritDoc}
	   */
	  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
	    // left intentionally blank
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @since 1.0.2
	   */
	  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
	    ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
	    scanner.setAnnotationClass(this.annotationClass);
	    scanner.setResourceLoader(this.applicationContext);
	    scanner.setApplicationContext(this.applicationContext);
	    scanner.registerFilters();
	    scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	  }

}
