/* 
 * "@(#)InterCtx.java	1.1	00/01/18 SMI"
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
 * A sample service provider that can act as an intermediate context
 * for any subinterface of Context.
 */

public class InterCtx extends FedCtx implements Resolver {

    // Constructors
    InterCtx(Hashtable inEnv) {
	super(inEnv);
    }

    protected InterCtx(FedCtx parent, String name, Hashtable inEnv, 
	Hashtable bindings) {
	super(parent, name, inEnv, bindings);
    }

    protected Context createCtx(FedCtx parent, String name, 
	Hashtable inEnv) {
	return new InterCtx(parent, name, inEnv, new Hashtable(11));
    }

    protected Context cloneCtx() {
	return new InterCtx(parent, myAtomicName, myEnv, bindings);
    }

    public ResolveResult resolveToClass(String name, Class ctxType)
	throws NamingException {
	    return resolveToClass(new CompositeName(name), ctxType);
    }

    public ResolveResult resolveToClass(Name name, Class ctxType) 
	throws NamingException {

	    // If we're it, can quit right now
	    if (ctxType.isInstance(this)) {
		return new ResolveResult(this, name);
	    }

	    try {
		Name[] nm = parseComponents(name);
		Name mine = nm[0];
		Name rest = nm[1];
		Object nxt = null;

		if (rest == null || rest.isEmpty()) {
		    // Terminal; just use head
		    nxt = lookup_internal(mine);
		} else if (rest.get(0).equals("") && rest.size() == 1) {
		    // Terminal NNS
		    nxt = lookup_nns(mine);
		} else if (mine.isEmpty() || isAllEmpty(rest)) {
		    // Intermediate; resolve mine as intermediate
		    Object obj = lookup_nns(mine);

		    // Skip leading /
		    throw fillInCPE(obj, mine, rest.getSuffix(1));
		} else {
		    // Intermediate; resolve mine as intermediate
		    Object obj = resolveIntermediate_nns(mine, rest);

		    throw fillInCPE(obj, mine, rest);
		}

		if (ctxType.isInstance(nxt)) {
		    return new ResolveResult(nxt, "");
		} else {
		    // We've resolved the entire composite name but
		    // cannot find the requested context type
		    throw new NotContextException(
			"Not instanceof " + ctxType);
		}
	    } catch (CannotProceedException e) {
		Context cctx = NamingManager.getContinuationContext(e);
		if (cctx instanceof Resolver) {
		    return ((Resolver)cctx).resolveToClass(e.getRemainingName(),
			ctxType);
		} else {
		    // Hit a nonResolver; give up
		    e.fillInStackTrace();
		    throw e;
		}
	    }
    }
}
