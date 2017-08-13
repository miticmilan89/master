/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rs.milanmitic.master.common.tag;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.tag.common.fmt.TimeZoneSupport;

import rs.milanmitic.master.common.ContextHolder;

/**
 * Support for tag handlers for &lt;formatDate&gt;, the date and time formatting tag in JSTL 1.0.
 * 
 * @author Jan Luehe
 */

public abstract class FormatDateSupport extends TagSupport {

	// *********************************************************************
	// Private constants

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DATE = "date";
	private static final String TIME = "time";
	private static final String DATETIME = "both";
	private static final String DATETIMESECOND = "both_sec";

	// *********************************************************************
	// Protected state

	protected Date value; // 'value' attribute
	protected String type; // 'type' attribute
	protected String pattern; // 'pattern' attribute
	protected transient Object timeZone; // 'timeZone' attribute
	protected String dateStyle; // 'dateStyle' attribute
	protected String timeStyle; // 'timeStyle' attribute

	// *********************************************************************
	// Private state

	private String var; // 'var' attribute
	private int scope; // 'scope' attribute

	// *********************************************************************
	// Constructor and initialization

	public FormatDateSupport() {
		super();
		init();
	}

	private void init() {
		type = null;
		dateStyle = null;
		timeStyle = null;
		pattern = null;
		var = null;
		value = null;
		timeZone = null;
		scope = PageContext.PAGE_SCOPE;
	}

	// *********************************************************************
	// Tag attributes known at translation time

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = Util.getScope(scope);
	}

	// *********************************************************************
	// Tag logic

	/*
	 * Formats the given date and time.
	 */
	@Override
	public int doEndTag() throws JspException {

		String formatted;

		if (value == null) {
			if (var != null) {
				pageContext.removeAttribute(var, scope);
			}
			return EVAL_PAGE;

		}
		String formatPattern = makeFormatPattern();

		if (StringUtils.isNotBlank(formatPattern)) {
			DateFormat formatter = new SimpleDateFormat(formatPattern);
			TimeZone tz = getTimeZone();
			if (tz != null) {
				formatter.setTimeZone(tz);
			}

			formatted = formatter.format(value);
		} else {
			formatted = value.toString();

		}


		if (var != null) {
			pageContext.setAttribute(var, formatted, scope);
		} else {
			try {
				pageContext.getOut().print(formatted);
			} catch (IOException i) {
				throw new JspTagException(i.toString(), i);
			}
		}

		return EVAL_PAGE;
	}

	private TimeZone getTimeZone() throws JspTagException {
		TimeZone tz;
		if (timeZone != null) {
			if (timeZone instanceof String) {
				tz = TimeZone.getTimeZone((String) timeZone);
			} else if (timeZone instanceof TimeZone) {
				tz = (TimeZone) timeZone;
			} else {
				throw new JspTagException(Resources.getMessage("FORMAT_DATE_BAD_TIMEZONE"));
			}
		} else {
			tz = getTimeZone(pageContext, this);
		}
		return tz;
	}

	private String makeFormatPattern() {
		String formatPattern = pattern;
		if (StringUtils.isBlank(formatPattern)) {

			if (StringUtils.isBlank(type) || DATETIME.equalsIgnoreCase(type)) {
				formatPattern = ContextHolder.getFormatPatterns().getDateOutputPattern() + " " + ContextHolder.getFormatPatterns().getTimeOutputPattern();

			} else if (DATE.equalsIgnoreCase(type)) {
				formatPattern = ContextHolder.getFormatPatterns().getDateOutputPattern();

			} else if (DATETIMESECOND.equalsIgnoreCase(type)) {
				formatPattern = ContextHolder.getFormatPatterns().getDateOutputPattern() + " " + ContextHolder.getFormatPatterns().getTimeOutputPatternWithSec();

			} else if (TIME.equalsIgnoreCase(type)) {
				formatPattern = ContextHolder.getFormatPatterns().getTimeOutputPattern();

			}
		}
		return formatPattern;
	}

	// Releases any resources we may have (or inherit)
	@Override
	public void release() {
		init();
	}

	// *********************************************************************
	// Private utility methods

	static TimeZone getTimeZone(PageContext pc, Tag fromTag) {
		TimeZone tz = null;

		Tag t = findAncestorWithClass(fromTag, TimeZoneSupport.class);
		if (t != null) {
			// use time zone from parent <timeZone> tag
			TimeZoneSupport parent = (TimeZoneSupport) t;
			tz = parent.getTimeZone();
		} else {
			// get time zone from configuration setting
			Object obj = Config.find(pc, Config.FMT_TIME_ZONE);
			if (obj != null) {
				if (obj instanceof TimeZone) {
					tz = (TimeZone) obj;
				} else {
					tz = TimeZone.getTimeZone((String) obj);
				}
			}
		}

		return tz;
	}
}
