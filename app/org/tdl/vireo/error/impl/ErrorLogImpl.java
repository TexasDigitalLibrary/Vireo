package org.tdl.vireo.error.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.event.ListSelectionEvent;

import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.error.ErrorReport;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.libs.F.IndexedEvent;
import play.mvc.Http.Request;

/**
 * Simple queue implementation of the ErrorLog interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 *
 */
public class ErrorLogImpl implements ErrorLog {

	// Spring dependencies
	public SecurityContext context;
	public int maxReports = 10;
	
	// State
	protected ConcurrentLinkedQueue<ErrorReport> reports = new ConcurrentLinkedQueue<ErrorReport>();
	
	/**
	 * Inject the security context.
	 * @param context The security context
	 */
	public void setSecurityContext(SecurityContext context) {
		this.context = context;
	}
	
	/**
	 * Set the maximum number of reports to keep in the queue;
	 * @param max
	 */
	public void setMaxReports(int max) {
		this.maxReports = max;
	}
	
	
	@Override
	public ErrorReport logError(Throwable exception, String where) {
		try {
			ErrorReport report = new ErrorReportImpl(
					context.getPerson(),
					where,
					exception);
		
			synchronized (this) {
				while (reports.size() >= maxReports) 
					reports.poll();	
				reports.add(report);
			}
		
			return report;
		} catch (RuntimeException re) {
			// Error, while reporting errors... *head explodes*
			
			// We'll just log the error and go on our merry way. 
			Logger.error(re, "Encountered an error while reporting another error that was encounted while '%s'.");
			return null;
		}
	}
	
	
	@Override
	public ErrorReport logError(Throwable exception, Request request) {

		String where = String.format(
				"HTTP request %s(): %s", 
				request.action,
				request.url);

		return logError(exception, where);
	}

	@Override
	public ErrorReport logError(Throwable exception, JobMetadata job) {

		String jobName = "unknown";
		if(null != job) {
			jobName = job.getName();
		}
		String message = String.format(
				"Background job: %s", 
				jobName);

		return logError(exception, message);
	}

	@Override
	public ErrorReport logError(Throwable exception) {
		return logError(exception, (String) null);
	}

	
	
	
	@Override
	public List<ErrorReport> getRecentErrorReports() {
		
		List<ErrorReport> copy =  new ArrayList<ErrorReport>(this.reports);
		Collections.reverse(copy);
		
		
		return copy;
	}
	
	
	@Override
	public synchronized void clearRecentErrorReports() {
		reports.clear();
	}
	
	
	

}
