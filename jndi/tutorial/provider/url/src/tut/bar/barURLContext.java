/* 
 * "@(#)barURLContext.java	1.1	00/01/18 SMI"
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

package tut.bar;

import javax.naming.*;
import javax.naming.spi.ResolveResult;
import java.util.*;

public class barURLContext 
extends tut.foo.fooURLContext implements tut.BarContext {

    protected barURLContext(Hashtable env) {
	super(env);
    }

    // Override to understand "bar:" scheme
    protected ResolveResult getRootURLContext(String url, Hashtable env) 
	throws NamingException {
	if (!url.startsWith("bar:/")) {
	    throw new IllegalArgumentException(url + " is not a bar URL");
	}

	String objName = url.length() > 5 ? url.substring(5) : null;

	// Represent object name as empty or single-component composite name.
	CompositeName remaining = new CompositeName();
	if (objName != null) {
	    remaining.add(objName);
	}

	// Get handle to the static namespace that we use for testing.
	// In an actual implementation, this might be the root
	// namespace on a particular server.
	Context ctx = tut.BarContextImpl.getStaticNamespace(env);

	return (new ResolveResult(ctx, remaining));
    }

    // Method that processes a name
    public Object barMethod(Name name) throws NamingException {
	if (name.size() == 1) {
	    return barMethod(name.get(0));
	} else {
	    Context ctx = getContinuationContext(name);
	    if (!(ctx instanceof tut.BarContext)) {
		throw new NotContextException(
		    "Cannot continue operation on nonBarContext");
	    }
	    try {
		return ((tut.BarContext)ctx).barMethod(name.getSuffix(1));
	    } finally {
		ctx.close();
	    }
	}
    }

    // Its string overload
    public Object barMethod(String name) throws NamingException {
	ResolveResult res = getRootURLContext(name, myEnv);
	tut.BarContext ctx = (tut.BarContext)res.getResolvedObj();
	try {
	    return ctx.barMethod(res.getRemainingName());
	} finally {
	    ctx.close();
	}
    }

    // Method that does not process names
    public String bazMethod() throws NamingException {
	return "baz URL version";  
    }
}
