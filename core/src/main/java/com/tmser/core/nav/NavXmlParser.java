package com.tmser.core.nav;
import java.io.IOException;

import org.apache.commons.digester3.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

	/**
	 * Parse an XML definitions file.
	 */
	public class NavXmlParser
	{

	    /** Associated digester. */
	  protected Digester digester;
	    /**
	     * Should we use a validating XML parser to read the configuration file.
	     * Default is <code>false</code>.
	     */
	   protected boolean validating = false;

	     /**
	      * Constructor.
	      * Creates a digester parser and initializes syntax rules.
	      */
	  public NavXmlParser()
	  {
		digester = new Digester();
		digester.setValidating(validating);
		digester.setNamespaceAware(true);
		digester.setUseContextClassLoader(true);//.tsp.common.nav
		//digester.register("-//res2area", "/com/tsp/common/nav/navs-1.0.dtd");
	    // Init syntax rules
		initDigester( digester );
	  }

	    /**
	     * Set digester validating flag.
	     */
	  public void setValidating( boolean validating )
	    {
	    digester.setValidating( validating);
	    }



	   /**
	    * Init digester.
	    * @param digester Digester instance to use.
	    */
	  protected void initDigester( Digester digester )
	  {
		  digester.addObjectCreate("navs/nav","com.tmser.core.nav.Nav");
		  digester.addSetProperties("navs/nav");
		  digester.addSetNext("navs/nav","addNavConfig", "com.tmser.core.nav.Nav");
		  digester.addObjectCreate("navs/nav/elem","com.tmser.core.nav.NavElem");
		  digester.addSetProperties("navs/nav/elem");
		  digester.addSetNext("navs/nav/elem","addElem", "com.tmser.core.nav.NavElem");
		  
	  }

	  /**
	   * Parse input reader and add encountered definitions to definitions set.
	   * @param in Input stream.
	   * @param definitions Xml Definitions set to which encountered definition are added.
	   * @throws IOException On errors during file parsing.
	   * @throws SAXException On errors parsing XML.
	 * @throws IOException 
	   */
	  public void parse(InputSource in, NavHolder navHolder ) throws SAXException, IOException
	  {
			try
		    {
		      // set first object in stack
		    //digester.clear();
		    digester.push(navHolder);
		      // parse
			digester.parse(in);
		 }catch (SAXException e)
		 {
			  //throw new ServletException( "Error while parsing " + mappingConfig, e);
		    throw e;
		}
	
	 }
	}
