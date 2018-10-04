/* 
 * "@(#)InitialBarContext.java	1.1	00/01/18 SMI"
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

import javax.naming.*;
import java.util.Hashtable;

public class InitialBarContext 
extends InitialContext implements BarContext {
    public InitialBarContext() throws NamingException {
	super();
    }

    public InitialBarContext(Hashtable env) throws NamingException {
	super(env);
    }

    // Method that processes a name
    public Object barMethod(Name name) throws NamingException {
	return getURLOrDefaultInitBarCtx(name).barMethod(name);
    }

    // Its string overload
    public Object barMethod(String name) throws NamingException {
	return getURLOrDefaultInitBarCtx(name).barMethod(name);
    }

    // Method that does not process names
    public String bazMethod() throws NamingException {
	return getDefaultInitBarCtx().bazMethod();
    }

    protected BarContext getDefaultInitBarCtx() throws NamingException {
	Context ctx = getDefaultInitCtx();
	if (!(ctx instanceof BarContext)) {
	    throw new NoInitialContextException("Not a BarContext");
	}
	return (BarContext)ctx;
    }

    protected BarContext getURLOrDefaultInitBarCtx(String name)
	throws NamingException {
	Context ctx = getURLOrDefaultInitCtx(name);
	if (!(ctx instanceof BarContext)) {
	    throw new NoInitialContextException("Not a BarContext");
	}
	return (BarContext)ctx;
    }

    protected BarContext getURLOrDefaultInitBarCtx(Name name)
	throws NamingException {
	Context ctx = getURLOrDefaultInitCtx(name);
	if (!(ctx instanceof BarContext)) {
	    throw new NoInitialContextException("Not a BarContext");
	}
	return (BarContext)ctx;
    }
}
