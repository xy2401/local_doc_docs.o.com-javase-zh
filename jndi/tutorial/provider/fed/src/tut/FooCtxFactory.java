/* 
 * "@(#)FooCtxFactory.java	1.1	00/01/18 SMI"
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

package tut;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.naming.*;
import javax.naming.spi.*;

/**
 * A sample object factory.that uses a URL context factory.
 */
public class FooCtxFactory implements ObjectFactory {
    public FooCtxFactory() {
    }

    public Object getObjectInstance(Object ref, Name name, Context nameCtx,
	Hashtable env) throws Exception {

	if ((ref instanceof Reference) &&
	    myClassName.equals(((Reference)ref).getFactoryClassName())) {
	    
	    // Create URL context factory
	    ObjectFactory factory = new tut.foo.fooURLContextFactory();

	    // Extract URL(s) from reference
	    String[] urls = getURLs((Reference)ref);

	    // Ask URL context factory to process URL(s)
	    return factory.getObjectInstance(urls, name, nameCtx, env);
	}

	// Not meant for this factory
	return null;
    }

    private final static String myClassName = FooCtxFactory.class.getName();

    /**
     * Returns the URLs contained within an LDAP reference.
     */
    private static String[] getURLs(Reference ref) throws NamingException {

	int size = 0;	// number of URLs
	String[] urls = new String[ref.size()];

	Enumeration addrs = ref.getAll();
	while (addrs.hasMoreElements()) {
	    RefAddr addr = (RefAddr)addrs.nextElement();

	    if ((addr instanceof StringRefAddr) &&
		addr.getType().equals("URL")) {

		urls[size++] = (String)addr.getContent();
	    }
	}
	if (size == 0) {
	    throw (new ConfigurationException(
		    "Reference contains no valid addresses"));
	}

	// Trim URL array down to size.
	if (size == ref.size()) {
	    return urls;
	}
	String[] urls2 = new String[size];
	System.arraycopy(urls, 0, urls2, 0, size);
	return urls2;
    }
}
