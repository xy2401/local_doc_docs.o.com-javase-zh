/* 
 * "@(#)fooURLContextFactory.java	1.1	00/01/18 SMI"
 * 
 * Copyright 1997, 1998, 1999 Sun Microsystems, Inc. All Rights
 * Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free,
 * license to use, modify and redistribute this software in source and
 * binary code form, provided that i) this copyright notice and license
 * appear on all copies of the software; and ii) Licensee does not 
 * utilize the software in a manner which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE 
 * HEREBY EXCLUDED.  SUN AND ITS LICENSORS SHALL NOT BE LIABLE 
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN 
 * NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER 
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT 
 * OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line
 * control of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and warrants
 * that it will not use or redistribute the Software for such purposes.  
 */

package tut.foo;

import javax.naming.*;
import javax.naming.spi.*;
import java.util.*;

/**
 * A sample URL context factory.
 */
public class fooURLContextFactory implements ObjectFactory {
    /**
     * Must have public no-arg constructor.
     */
    public fooURLContextFactory() {
    }

    /**
     *  The "foo" url is of the form foo:/<name in HierCtx namespace>.
     */
    public Object getObjectInstance(Object urlInfo, Name name, Context nameCtx,
	Hashtable env) throws Exception {

	    // Case 1: urlInfo is null
	    // This means to create a URL context that can accept 
	    // arbitrary "foo" URLs.
	    if (urlInfo == null) {
		return createURLContext(env);
	    }

	    // Case 2: urlInfo is a single string
	    // This means to create/get the object named by urlInfo
	    if (urlInfo instanceof String) {
		Context urlCtx = createURLContext(env);
		try {
		    return urlCtx.lookup((String)urlInfo);
		} finally {
		    urlCtx.close();
		}
	    } 

	    // Case 3: urlInfo is an array of strings
	    // This means each entry in array is equal alternative; create/get
	    // the object named by one of the URls
	    if (urlInfo instanceof String[]) {

		// Try each URL until lookup() succeeds for one of them.
		// If all URLs fail, throw one of the exceptions arbitrarily.
		String[] urls = (String[])urlInfo;
		if (urls.length == 0) {
		    throw (new ConfigurationException(
			"fooURLContextFactory: empty URL array"));
		}
		Context urlCtx = createURLContext(env);
		try {
		    NamingException ne = null;
		    for (int i = 0; i < urls.length; i++) {
			try {
			    return urlCtx.lookup(urls[i]);
			} catch (NamingException e) {
			    ne = e;
			}
		    }
		    throw ne;
		} finally {
		    urlCtx.close();
		}
	    } 

	    // Case 4: urlInfo is of an unknown type
	    // Provider-specific action: reject input

	    throw new IllegalArgumentException(
		"argument must be a foo URL string or an array of them");
    }

    protected Context createURLContext(Hashtable env) {
	return new fooURLContext(env);
    }
}
