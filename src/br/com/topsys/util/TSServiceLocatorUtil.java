/*
 * Created on 25/05/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package br.com.topsys.util;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;


import br.com.topsys.exception.TSSystemException;



/**
 * @author andre
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * This class is an implementation of the Service Locator pattern. It is used to
 * looukup resources such as EJBHomes, JMS Destinations, etc. This
 * implementation uses the "singleton" strategy and also the "caching" strategy.
 * This implementation is intended to be used on the web tier and not on the ejb
 * tier.
 */
public final class TSServiceLocatorUtil {

	private InitialContext ic;

	private Map cache; 

	private static TSServiceLocatorUtil me;

	@SuppressWarnings("unchecked")
	private TSServiceLocatorUtil(){
		try {
			ic = new InitialContext();
			cache = Collections.synchronizedMap(new HashMap());
		} catch (Exception ne) {
			TSLogUtil.getInstance().severe(ne.getMessage());
			throw new TSSystemException(ne);
		}

	}

	/**
	 * 
	 * @return
	 * @throws br.com.topsys.exception.TSServiceLocatorException
	 */
	public static TSServiceLocatorUtil getInstance(){
		if (me == null) {
				
			me = new TSServiceLocatorUtil();
			
		}
		return me;
	}



	/**
	 * This method obtains the datasource itself for a caller
	 * 
	 * @return the DataSource corresponding to the name parameter
	 */
	@SuppressWarnings("unchecked")
	public DataSource getDataSource(String dataSourceName) {
		DataSource dataSource = null;
		try {
			if (cache.containsKey(dataSourceName)) {
				dataSource = (DataSource) cache.get(dataSourceName);
			} else {
				dataSource = (DataSource) ic.lookup(dataSourceName);
				cache.put(dataSourceName, dataSource);
			}
		} catch (Exception ne) {
			TSLogUtil.getInstance().severe(ne.getMessage());
			throw new TSSystemException(ne);
		} 
		return dataSource;
	}
	

	/**
	 * @return the URL value corresponding to the env entry name.
	 */
	public URL getUrl(String envName){
		URL url = null;
		try {
			url = (URL) ic.lookup(envName);
		} catch (Exception ne) {
			TSLogUtil.getInstance().severe(ne.getMessage());
			throw new TSSystemException(ne);
		} 
		return url;
	}

	/**
	 * @return the boolean value corresponding to the env entry such as
	 *         SEND_CONFIRMATION_MAIL property.
	 */
	public boolean getBoolean(String envName) {
		Boolean bool = null;
		try {
			bool = (Boolean) ic.lookup(envName);
		} catch (Exception ne) {
			TSLogUtil.getInstance().severe(ne.getMessage());
			throw new TSSystemException(ne);
		} 
		return bool.booleanValue();
	}

	/**
	 * @return the String value corresponding to the env entry name.
	 */
	public String getString(String envName) {
		String envEntry = null;
		try {
			envEntry = (String) ic.lookup(envName);
		} catch (Exception ne) {
			TSLogUtil.getInstance().severe(ne.getMessage());
			throw new TSSystemException(ne);
		} 
		return envEntry;
	}

}