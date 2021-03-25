/*
 * Created on 30/03/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package br.com.topsys.exception;



/**
 *
 * 
 * @author atopazio
 */

@SuppressWarnings("serial")
public class TSApplicationException extends Exception {

	
	
	
	public TSApplicationException(Exception e) {
		super(e);
	}

	
	public TSApplicationException(String chave) {
		super(chave);
	}
	
	
	public TSApplicationException(String chave,Exception e) {
		super(chave,e);
	}

}