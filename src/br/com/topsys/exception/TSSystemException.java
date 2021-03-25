/*
 * Created on 20/03/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package br.com.topsys.exception;


@SuppressWarnings("serial")
public class TSSystemException extends RuntimeException {

	
	public TSSystemException(Exception e) {
		super(e);
	}

	
	public TSSystemException(String x, Exception e) {
		super(x, e);
	}

}