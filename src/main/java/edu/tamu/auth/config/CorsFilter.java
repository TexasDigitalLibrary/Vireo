/* 
 * CorsFilter.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.auth.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 
 * Cross-Origin Resource Sharing filter.
 * 
 * @author
 *
 */
@Component
public class CorsFilter implements Filter {

	@Value("${auth.security.allow-access}")
	private String[] hosts;
	
	/**
	 * Filter to add appropriate access control.
	 *
	 * @param       req    			ServletRequest
	 * @param       res    			ServletResponse
	 * @param       chain    		FilterChain
	 * 
	 * @exception   IOException
	 * @exception   ServletException
	 * 
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		
		for(String host : hosts) {
			if(host.equals(request.getHeader("Origin"))) {
				response.setHeader("Access-Control-Allow-Origin", host);
			}
		}

		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with, jwt");
		chain.doFilter(req, res);
	}

	/**
	 * Initialize CORS filter.
	 *
	 * @param       filterConfig    FilterConfig
	 *
	 */
	public void init(FilterConfig filterConfig) {}

	/**
	 * Destroy method.
	 *
	 */
	public void destroy() {}

}