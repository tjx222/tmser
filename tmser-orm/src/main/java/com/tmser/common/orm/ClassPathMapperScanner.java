package com.tmser.common.orm;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

/**
 * bo 自动扫描器
 * 默认扫描<quote>javax.persistence.Entity</quote>注解的BO 类
 * @author tjx
 * @version 2.0
 * 2014-1-15
 */
public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
	protected static final Logger logger = LoggerFactory.getLogger(ClassPathMapperScanner.class);
	
	private Class<? extends Annotation> annotationClass;
	
	private ApplicationContext applicationContext;
	
	private MapperContainer mapperContainer;
	
	public void setMapperContainer(MapperContainer mapperContainer) {
		this.mapperContainer = mapperContainer;
	}

	/**
	   * 设置要扫描的注解.
	   * <p>
	   * 在设定的包下所有被该注解注释的类都将被扫描.
	   * <p>
	   *
	   * @param annotationClass annotation class
	   */
	  public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
	    this.annotationClass = annotationClass;
	  }
	  
	 /**
	 * @param registry
	 */
	public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	  /**
	   * {@inheritDoc}
	   */
	  @Override
	  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
	    return beanDefinition.getMetadata().isIndependent();
	  }

	  /**
	   * 注册扫描过滤器
	   */
	  public void registerFilters() {
	    boolean acceptAllInterfaces = true;

	    // if specified, use the given annotation and / or marker interface
	    if (this.annotationClass != null) {
	      addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
	      acceptAllInterfaces = false;
	    }
	    
	    if (acceptAllInterfaces) {
	      // default include filter that accepts all classes
	      addIncludeFilter(new TypeFilter() {
	        @Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
	          return true;
	        }
	      });
	    }

	    // exclude package-info.java
	    addExcludeFilter(new TypeFilter() {
	      @Override
		public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
	        String className = metadataReader.getClassMetadata().getClassName();
	        return className.endsWith("package-info");
	      }
	    });
	  }
	  
	  /**
	   * 调用父类扫描方法进行bean 扫描，解析扫描到的bean。并将解析出的mapper 信息存储到
	   * @see MapperContainer 中
	   */
	  @Override
	  public Set<BeanDefinitionHolder> doScan(String... basePackages) {
	    Set<BeanDefinition> beanDefinitions = scanPackages(basePackages);

	    if (beanDefinitions.isEmpty()) {
	      logger.warn("No mapper was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
	    } else {
	      MapperContainer container = getMapperContainer();
	      for (BeanDefinition beanDefinition : beanDefinitions) {
	    	  ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition) beanDefinition;

	        if (logger.isDebugEnabled()) {
	          logger.debug("Creating Mapper with name '" + beanDefinition.getBeanClassName());
	        }
	        Table t = OrmHelper.parseTable(definition);
	        if(t != null){
	        	String name = definition.getBeanClass().getSimpleName();
	        	Table r = container.getTable(name);
	        	if(r == null){
	        		container.addTable(name, t);
	        	}else{//bo's class simple name can't be repeat.
	        		logger.warn("bo [{}] already exists ! the old is [{}] .",
	        				definition.getBeanClassName(),r.getTableName());
	        	}
	        	
	        }
	      }
	      
	      logger.info("loaded "+container.size()+" bo Mapper");
	    }

	    return null;
	  }
	  
	  private Set<BeanDefinition> scanPackages(String... basePackages){
		  Assert.notEmpty(basePackages, "At least one base package must be specified");
		Set<BeanDefinition> beanDefinitions = new LinkedHashSet<BeanDefinition>();
		for (String basePackage : basePackages) {
			Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			for (BeanDefinition candidate : candidates) {
				if (candidate instanceof AnnotatedBeanDefinition) {
					AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
				}
				beanDefinitions.add(candidate);
			}
		}
		return beanDefinitions;
	  }
	  
	  private MapperContainer getMapperContainer(){
		if(this.mapperContainer != null){
			  return this.mapperContainer;
		}
		MapperContainer mc = null;
		try {
			mc = applicationContext.getBean(MapperContainer.class);
			this.mapperContainer = mc;
		} catch (Exception e) {
			getRegistry().registerBeanDefinition(DefaultMapperContainer.class.getSimpleName(),
						  BeanDefinitionBuilder.genericBeanDefinition(DefaultMapperContainer.class).getRawBeanDefinition());
			this.mapperContainer = applicationContext.getBean(MapperContainer.class);
		}
		 
		  return this.mapperContainer;
	  }
}
