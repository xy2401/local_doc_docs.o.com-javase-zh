/* 
 * "@(#)ContinuationBarCtx.java	1.1	00/01/18 SMI"
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
import javax.naming.spi.*;
import java.util.*;

/**
 * A sample continuation context for BarContext, which is a
 * subinterface of Context.
 */

public class ContinuationBarCtx implements Resolver, tut.BarContext {
    protected Context cctx;
    protected CannotProceedException cpe;

    // Public method
    public tut.BarContext getContinuationBarContext(CannotProceedException cpe) {
	return new ContinuationBarCtx(cpe);
    }

    ContinuationBarCtx(CannotProceedException cpe) {
	this.cpe = cpe;
    }

    protected ResolveResult getTargetContext(Name name)	throws NamingException {

	Context ctx = getTargetContext();

	if (ctx instanceof tut.BarContext)
	    return new ResolveResult(ctx, name);

	// found resolve, ask it to find BarContext
	if (ctx instanceof Resolver) {
	    Resolver res = (Resolver)ctx;
	    return res.resolveToClass(name, tut.BarContext.class);
	}

	// Resolve all the way using lookup().  This may allow the operation
	// to succeed if it doesn't require the penultimate context.
	Object ultimate = ctx.lookup(name);
	if (ultimate instanceof tut.BarContext) {
	    return new ResolveResult(ultimate, new CompositeName());
	}

	throw (NamingException)cpe.fillInStackTrace();
    }

    // Gets the default target context and cache it
    protected Context getTargetContext() throws NamingException {
	if (cctx == null) {
	    cctx = NamingManager.getContinuationContext(cpe);
	}
	return cctx;
    }

    public Object barMethod(Name name) throws NamingException {
	ResolveResult res = getTargetContext(name);
	return ((tut.BarContext)res.getResolvedObj()).barMethod(
	    res.getRemainingName());
    }

    public Object barMethod(String name) throws NamingException {
	return barMethod(new CompositeName(name));
    }

    // Method that does not process names
    public String bazMethod() throws NamingException {
	Context ctx = getTargetContext();
	if (ctx instanceof tut.BarContext) {
	    return ((tut.BarContext)ctx).bazMethod();
	} else {
	    throw (CannotProceedException)cpe.fillInStackTrace();
	}
    }

    // All other context methods can just use getTargetContext()
    public Object lookup(Name name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.lookup(name);
    }

    public Object lookup(String name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.lookup(name);
    }

    public void bind(Name name, Object newObj) throws NamingException {
	Context ctx = getTargetContext();
	ctx.bind(name, newObj);
    }

    public void bind(String name, Object newObj) throws NamingException {
	Context ctx = getTargetContext();
	ctx.bind(name, newObj);
    }

    public void rebind(Name name, Object newObj) throws NamingException {
	Context ctx = getTargetContext();
	ctx.rebind(name, newObj);
    }
    public void rebind(String name, Object newObj) throws NamingException {
	Context ctx = getTargetContext();
	ctx.rebind(name, newObj);
    }

    public void unbind(Name name) throws NamingException {
	Context ctx = getTargetContext();
	ctx.unbind(name);
    }
    public void unbind(String name) throws NamingException {
	Context ctx = getTargetContext();
	ctx.unbind(name);
    }

    public void rename(Name name, Name newName) throws NamingException {
	Context ctx = getTargetContext();
	ctx.rename(name, newName);
    }
    public void rename(String name, String newName) throws NamingException {
	Context ctx = getTargetContext();
	ctx.rename(name, newName);
    }
	    
    public NamingEnumeration list(Name name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.list(name);
    }
    public NamingEnumeration list(String name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.list(name);
    }


    public NamingEnumeration listBindings(Name name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.listBindings(name);
    }

    public NamingEnumeration listBindings(String name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.listBindings(name);
    }

    public void destroySubcontext(Name name) throws NamingException {
	Context ctx = getTargetContext();
	ctx.destroySubcontext(name);
    }
    public void destroySubcontext(String name) throws NamingException {
	Context ctx = getTargetContext();
	ctx.destroySubcontext(name);
    }
	    
    public Context createSubcontext(Name name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.createSubcontext(name);
    }
    public Context createSubcontext(String name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.createSubcontext(name);
    }
	    
    public Object lookupLink(Name name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.lookupLink(name);
    }
    public Object lookupLink(String name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.lookupLink(name);
    }

    public NameParser getNameParser(Name name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.getNameParser(name);
    }

    public NameParser getNameParser(String name) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.getNameParser(name);
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
	Context ctx = getTargetContext();
	return ctx.composeName(name, prefix);
    }

    public String composeName(String name, String prefix)
	    throws NamingException {
	Context ctx = getTargetContext();
	return ctx.composeName(name, prefix);
    }

    public Object addToEnvironment(String propName, Object value)
	throws NamingException {
	Context ctx = getTargetContext();
	return ctx.addToEnvironment(propName, value);
    }

    public Object removeFromEnvironment(String propName)
	throws NamingException {
	Context ctx = getTargetContext();
	return ctx.removeFromEnvironment(propName);
    }

    public Hashtable getEnvironment() throws NamingException {
	Context ctx = getTargetContext();
	return ctx.getEnvironment();
    }

    public String getNameInNamespace() throws NamingException {
	Context ctx = getTargetContext();
	return ctx.getNameInNamespace();
    }

    public ResolveResult resolveToClass(Name name, Class contextType)
	    throws NamingException {
	Context ctx = getTargetContext();
	if (ctx instanceof Resolver) {
	    return ((Resolver)ctx).resolveToClass(name, contextType);
	} else {
	    throw (NamingException)cpe.fillInStackTrace();
	}
    }

    public ResolveResult resolveToClass(String name, Class contextType)
	    throws NamingException {
	Context ctx = getTargetContext();
	if (ctx instanceof Resolver) {
	    return ((Resolver)ctx).resolveToClass(name, contextType);
	} else {
	    throw (NamingException)cpe.fillInStackTrace();
	}
    }

    public void close() throws NamingException {
	cpe = null;
	if (cctx != null) {
	    cctx.close();
	    cctx = null;
	}
    }
}
