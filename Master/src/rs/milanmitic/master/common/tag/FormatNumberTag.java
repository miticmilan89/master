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

/**
 * <p>
 * A handler for &lt;formatNumber&gt; that accepts attributes as Strings and evaluates them as expressions at runtime.
 * </p>
 * 
 * @author Jan Luehe
 */

public class FormatNumberTag extends FormatNumberSupport {
	// *********************************************************************
	// Accessor methods

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 'value' attribute
	public void setValue(Object value)  {
		this.value = value;
		this.valueSpecified = true;
	}

	// 'type' attribute
	public void setType(String type)  {
		this.type = type;
	}

	// 'pattern' attribute
	public void setPattern(String pattern)  {
		this.pattern = pattern;
	}

	// 'currencyCode' attribute
	public void setCurrencyCode(String currencyCode)  {
		this.currencyCode = currencyCode;
	}

	// 'currencySymbol' attribute
	public void setCurrencySymbol(String currencySymbol)  {
		this.currencySymbol = currencySymbol;
	}

	// 'groupingUsed' attribute
	public void setGroupingUsed(boolean isGroupingUsed)  {
		this.isGroupingUsed = isGroupingUsed;
		this.groupingUsedSpecified = true;
	}

	// 'maxIntegerDigits' attribute
	public void setMaxIntegerDigits(int maxDigits)  {
		this.maxIntegerDigits = maxDigits;
		this.maxIntegerDigitsSpecified = true;
	}

	// 'minIntegerDigits' attribute
	public void setMinIntegerDigits(int minDigits)  {
		this.minIntegerDigits = minDigits;
		this.minIntegerDigitsSpecified = true;
	}

	// 'maxFractionDigits' attribute
	public void setMaxFractionDigits(int maxDigits)  {
		this.maxFractionDigits = maxDigits;
		this.maxFractionDigitsSpecified = true;
	}

	// 'minFractionDigits' attribute
	public void setMinFractionDigits(int minDigits)  {
		this.minFractionDigits = minDigits;
		this.minFractionDigitsSpecified = true;
	}
}
