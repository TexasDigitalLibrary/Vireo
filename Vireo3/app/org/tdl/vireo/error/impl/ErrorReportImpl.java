package org.tdl.vireo.error.impl;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.tdl.vireo.error.ErrorReport;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;

import play.Logger;
import play.mvc.Http.Request;

/**
 * A very basic implementation of the error report interface. This class keeps
 * track of the details about an error.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class ErrorReportImpl implements ErrorReport {

	// Our state
	public final Date when;
	public final String who;
	public final String where;
	public final String what;
	public final String trace;
	
	/**
	 * Generate a new error report.
	 * 
	 * @param user
	 *            The user who encountered the error.
	 * @param where
	 *            Where the error occured.
	 * @param exception
	 *            The exception encountered.
	 */
	public ErrorReportImpl(Person user, String where, Throwable exception) {
		this.when = new Date();
		
		if (user != null) {
			this.who = String.format("%s (%d: %s)",
					user.getFormattedName(NameFormat.FIRST_LAST), 
					user.getId(), 
					user.getEmail()
					);
		} else {
			this.who = null;
		}
		
		this.where = where;
	
		if (exception != null)
			this.what = exception.getClass().getName();
		else
			this.what = null;
		this.trace = ExceptionUtils.getStackTrace(exception);
	}
	
	@Override
	public Date getWhen() {
		return when;
	}
	
	@Override
	public String getWho() {
		return who;
	}

	@Override
	public String getWhere() {
		return where;
	}
	
	@Override
	public String getWhat() {
		return what;
	}

	@Override
	public String getStackTrace() {
		return trace;
	}

}
