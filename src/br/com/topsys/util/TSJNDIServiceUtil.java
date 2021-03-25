/*
 * Created on 19/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package br.com.topsys.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.topsys.exception.TSSystemException;



/**
 * @author andre.topazio
 * 
 */
public final class TSJNDIServiceUtil {

	// Propriedade que contem o contexto inicial
	private static Context context;

	static {
		try {
			context = new InitialContext();
		} catch (Exception e) {
			TSLogUtil.getInstance().severe(e.getMessage());
		}
	}

	
	public static Context getContext() {
		return context;
	}


	public static Object getJNDIValue(final String aVariable){
		Object obj = null;
		try {
			obj = context.lookup("java:comp/env/" + aVariable);

		} catch (NamingException e) {
			TSLogUtil.getInstance().severe(e.getMessage());
			throw new TSSystemException(e);
		}

		return obj;
	}

	
	public static Object getEJB(final String aVariable){

		Object obj = null;
		try {
			obj = context.lookup(aVariable);

		} catch (NamingException e) {
			TSLogUtil.getInstance().severe(e.getMessage());
			throw new TSSystemException(e);
		}

		return obj;
	}

}