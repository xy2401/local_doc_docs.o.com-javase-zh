/* 
 * "@(#)FedCtx.java	1.2	00/04/28 SMI"
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
 * A sample service provider that supports federation.
 * The namespace of this context implementation is flat.
 */

public class FedCtx implements Context {
    private static Name NNS_NAME; 
    protected Hashtable myEnv;
    protected Hashtable bindings = new Hashtable(11);
    protected final static NameParser myParser = new FedParser();
    protected FedCtx parent = null;
    protected String myAtomicName = null;

    static {
	try {
	    NNS_NAME = new CompositeName().add("");
	} catch (Exception e) {}
    }

    FedCtx(Hashtable inEnv) {
        myEnv = (inEnv != null)
            ? (Hashtable)(inEnv.clone()) 
            : null;
    }

    protected FedCtx(FedCtx parent, String name, Hashtable inEnv, 
	Hashtable bindings) {
	this(inEnv);
	this.parent = parent;
	myAtomicName = name;
	this.bindings = (Hashtable)bindings.clone();
    }

    protected Context createCtx(FedCtx parent, String name, 
	Hashtable inEnv) {
	return new FedCtx(parent, name, inEnv, new Hashtable(11));
    }

    protected Context cloneCtx() {
	return new FedCtx(parent, myAtomicName, myEnv, bindings);
    }

    /**
     * Utility method for processing composite/compound name.
     * Override to do appropriate parsing.
     *
     * @param name The non-null composite or compound name to process.
     * @return A pair of head/tail where head should be processed
     *    by this naming system and tail by the next naming system
     */
    protected Name[] parseComponents(Name name) throws NamingException {
	Name head, tail;
	if (name instanceof CompositeName) {
	    int separator;
	    // if no name to parse, or if we're already at boundary
	    if (name.isEmpty() ||  name.get(0).equals("")) {
		separator = 0;
	    } else {
		separator = 1;
	    }

	    head = name.getPrefix(separator);
	    tail = name.getSuffix(separator);
	} else {
	    // treat like compound name
	    head = new CompositeName().add(name.toString());
	    tail = null;
	}

	return new Name[]{head, tail};
    }

    public Object lookup(Name name) throws NamingException {
	try {
	    Name[] nm = parseComponents(name);
	    Name mine = nm[0];
	    Name rest = nm[1];

	    if (rest == null || rest.isEmpty()) {
		// Terminal; just use head
		return lookup_internal(mine);
	    } else if (rest.get(0).equals("") && rest.size() == 1) {
		// Terminal NNS
		return lookup_nns(mine);
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
	} catch (CannotProceedException e) {
	    // e.printStackTrace();
	    Context cctx = NamingManager.getContinuationContext(e);
	    return cctx.lookup(e.getRemainingName());
	}
    }

    protected Object lookup_internal(Name name) throws NamingException {
        if (name.isEmpty()) {
            // Asking to look up this context itself.  Create and return
            // a new instance with its own independent environment.
            return cloneCtx();
        } 

	String atom = getInternalName(name);
	Object obj = bindings.get(atom);

	if (obj == null) {
	    throw new NameNotFoundException(name + " not found");
	}

	// Call getObjectInstance for using any object factories
	try {
	    return NamingManager.getObjectInstance(obj, name,
		this, myEnv);
	} catch (Exception e) {
	    NamingException ne = new NamingException(
		"getObjectInstance failed");
	    ne.setRootCause(e);
	    throw ne;
	}
    }

    protected Object lookup_nns(Name name) throws NamingException {
	processJunction_nns(name);
	return null;
    }

    public Object lookup(String name) throws NamingException {
	return lookup(new CompositeName(name));
    }

    public void bind(String name, Object obj) throws NamingException {
	bind(new CompositeName(name), obj);
    }

    public void bind(Name name, Object bindObj) throws NamingException {
	try {
	    Name[] nm = parseComponents(name);
	    Name mine = nm[0];
	    Name rest = nm[1];

	    if (rest == null || rest.isEmpty()) {
		// Terminal; just use head
		bind_internal(mine, bindObj);
	    } else if (rest.get(0).equals("") && rest.size() == 1) {
		// Terminal NNS
		bind_nns(mine, bindObj);
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
	} catch (CannotProceedException e) {
	    Context cctx = NamingManager.getContinuationContext(e);
	    cctx.bind(e.getRemainingName(), bindObj);
	}
    }

    private void bind_internal(Name name, Object obj) 
	throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot bind empty name");
        }

	// Extract components that belong to this namespace
	String atom = getInternalName(name);
	if (bindings.get(atom) != null) {
	    throw new NameAlreadyBoundException(
		"Use rebind to override");
	}

	// Call getStateToBind for using any state factories
	obj = NamingManager.getStateToBind(obj, name, this, myEnv);

	// Add object to internal data structure
	bindings.put(atom, obj);
    }

    private void bind_nns(Name name, Object obj) throws NamingException {
	processJunction_nns(name);
    }

    public void rebind(String name, Object obj) throws NamingException {
	rebind(new CompositeName(name), obj);
    }

    public void rebind(Name name, Object bindObj) throws NamingException {
	try {
	    Name[] nm = parseComponents(name);
	    Name mine = nm[0];
	    Name rest = nm[1];

	    if (rest == null || rest.isEmpty()) {
		// Terminal; just use head
		rebind_internal(mine, bindObj);
	    } else if (rest.get(0).equals("") && rest.size() == 1) {
		// Terminal NNS
		rebind_nns(mine, bindObj);
	    } else if (mine.isEmpty() || isAllEmpty(rest)) {
		// Intermediate; resolve mine as intermediate
		Object obj = lookup_nns(mine);

		throw fillInCPE(obj, mine, rest.getSuffix(1));
	    } else {
		// Intermediate; resolve mine as intermediate
		Object obj = resolveIntermediate_nns(mine, rest);

		throw fillInCPE(obj, mine, rest);
	    }
	} catch (CannotProceedException e) {
	    Context cctx = NamingManager.getContinuationContext(e);
	    cctx.rebind(e.getRemainingName(), bindObj);
	}
    }

    private void rebind_nns(Name name, Object obj) 
	throws NamingException {
	processJunction_nns(name);
    }

    private void rebind_internal(Name name, Object obj) 
	throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot bind empty name");
        }

	// Extract components that belong to this namespace
	String atom = getInternalName(name);

	// Call getStateToBind for using any state factories
	obj = NamingManager.getStateToBind(obj, name, this, myEnv);

	// Add object to internal data structure
	bindings.put(atom, obj);
    }

    public void unbind(String name) throws NamingException {
	unbind(new CompositeName(name));
    }

    public void unbind(Name name) throws NamingException {
	try {
	    Name[] nm = parseComponents(name);
	    Name mine = nm[0];
	    Name rest = nm[1];

	    if (rest == null || rest.isEmpty()) {
		// Terminal; just use head
		unbind_internal(mine);
	    } else if (rest.get(0).equals("") && rest.size() == 1) {
		// Terminal NNS
		unbind_nns(mine);
	    } else if (mine.isEmpty() || isAllEmpty(rest)) {
		// Intermediate; resolve mine as intermediate
		Object obj = lookup_nns(mine);

		throw fillInCPE(obj, mine, rest.getSuffix(1));
	    } else {
		// Intermediate; resolve mine as intermediate
		Object obj = resolveIntermediate_nns(mine, rest);

		throw fillInCPE(obj, mine, rest);
	    }
	} catch (CannotProceedException e) {
	    Context cctx = NamingManager.getContinuationContext(e);
	    cctx.unbind(e.getRemainingName());
	}
    }

    private void unbind_nns(Name name) throws NamingException {
	processJunction_nns(name);
    }

    private void unbind_internal(Name name) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot unbind empty name");
        }

	String atom = getInternalName(name);

	// Remove object from internal data structure
	bindings.remove(atom);
    }

    public void rename(String oldname, String newname) 
	throws NamingException {
	rename(new CompositeName(oldname), new CompositeName(newname));
    }

    public void rename(Name oldname, Name newname) 
	throws NamingException {
	try {
	    Name[] nm = parseComponents(oldname);
	    Name mine = nm[0];
	    Name rest = nm[1];

	    Name[] newNm = parseComponents(newname);
	    Name newMine = newNm[0];
	    Name newRest = newNm[1];

	    if (rest == null || rest.isEmpty()) {
		// Terminal; just use head
		rename_internal(mine, newname);
	    } else if (rest.get(0).equals("") && rest.size() == 1) {
		// Terminal NNS
		rename_nns(mine, newname);
	    } else if (mine.isEmpty() || isAllEmpty(rest)) {
		if (!mine.equals(newMine)) {
		    throw new InvalidNameException(
			"cannot handle rename across federations");
		}

		// Intermediate; resolve mine as intermediate
		Object obj = lookup_nns(mine);

		// Skip leading /
		CannotProceedException cpe = 
		    fillInCPE(obj, mine, rest.getSuffix(1));
		cpe.setRemainingNewName(newRest);
		throw cpe;
	    } else {
		if (!mine.equals(newMine)) {
		    throw new InvalidNameException(
			"cannot handle rename across federations");
		}

		// Intermediate; resolve mine as intermediate
		Object obj = resolveIntermediate_nns(mine, rest, newRest);

		CannotProceedException cpe = fillInCPE(obj, mine, rest);
		cpe.setRemainingNewName(newRest);
		throw cpe;
	    }
	} catch (CannotProceedException e) {
	    Context cctx = NamingManager.getContinuationContext(e);
	    cctx.rename(e.getRemainingName(), e.getRemainingNewName());
	}
    }

    private void rename_nns(Name oldname, Name newname) 
	throws NamingException {
	    try {
		processJunction_nns(oldname);
	    } catch (CannotProceedException e) {
		e.setRemainingNewName(newname);
		throw e;
	    }
    }

    private void rename_internal(Name oldname, Name newname) 
	throws NamingException {
        if (oldname.isEmpty() || newname.isEmpty()) {
            throw new InvalidNameException("Cannot rename empty name");
        }

	String oldatom = getInternalName(oldname);
	String newatom = getInternalName(newname);

	// Check if new name exists
	if (bindings.get(newatom) != null) {
	    throw new NameAlreadyBoundException(newname.toString() +
		" is already bound");
	}

	// Check if old name is bound
	Object oldBinding = bindings.remove(oldatom);
	if (oldBinding == null) {
	    throw new NameNotFoundException(oldname.toString() + 
		" not bound");
	}
	
	bindings.put(newatom, oldBinding);
    }

    public NamingEnumeration list(String name) throws NamingException {
	return list(new CompositeName(name));
    }

    public NamingEnumeration list(Name name) throws NamingException {
	try {
	    Name[] nm = parseComponents(name);
	    Name mine = nm[0];
	    Name rest = nm[1];

	    if (rest == null || rest.isEmpty()) {
		// Terminal; just use head
		return list_internal(mine);
	    } else if (rest.get(0).equals("") && rest.size() == 1) {
		// Terminal NNS
		return list_nns(mine);
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
	} catch (CannotProceedException e) {
	    Context cctx = NamingManager.getContinuationContext(e);
	    return cctx.list(e.getRemainingName());
	}
    }

    private NamingEnumeration list_nns(Name name) 
	throws NamingException {
	    processJunction_nns(name);
	    return null;
    }

    private NamingEnumeration list_internal(Name name) 
	throws NamingException {
        if (name.isEmpty()) {
            // listing this context
            return new ListOfNames(bindings.keys());
        } 

        // Perhaps 'name' names a context
        Object target = lookup_internal(name);
        if (target instanceof Context) {
            return ((Context)target).list("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    public NamingEnumeration listBindings(String name) 
	throws NamingException {
	return listBindings(new CompositeName(name));
    }

    public NamingEnumeration listBindings(Name name) 
	throws NamingException {
	try {
	    Name[] nm = parseComponents(name);
	    Name mine = nm[0];
	    Name rest = nm[1];

	    if (rest == null || rest.isEmpty()) {
		// Terminal; just use head
		return listBindings_internal(mine);
	    } else if (rest.get(0).equals("") && rest.size() == 1) {
		// Terminal NNS
		return listBindings_nns(mine);
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
	} catch (CannotProceedException e) {
	    Context cctx = NamingManager.getContinuationContext(e);
	    return cctx.listBindings(e.getRemainingName());
	}
    }

    private NamingEnumeration listBindings_nns(Name name) 
	throws NamingException {
	    processJunction_nns(name);
	    return null;
    }

    private NamingEnumeration listBindings_internal(Name name) 
	throws NamingException {
        if (name.isEmpty()) {
            // listing this context
            return new ListOfBindings(bindings.keys());
        } 

        // Perhaps 'name' names a context
        Object target = lookup_internal(name);
        if (target instanceof Context) {
            return ((Context)target).listBindings("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    public void destroySubcontext(String name) throws NamingException {
	destroySubcontext(new CompositeName(name));
    }

    public void destroySubcontext(Name name) throws NamingException {
	throw new OperationNotSupportedException("Flat namespace");
    }

    public Context createSubcontext(String name) throws NamingException {
	return createSubcontext(new CompositeName(name));
    }

    public Context createSubcontext(Name name) throws NamingException {
	throw new OperationNotSupportedException("Flat namespace");
    }

    public Object lookupLink(String name) throws NamingException {
	return lookupLink(new CompositeName(name));
    }

    public Object lookupLink(Name name) throws NamingException {
        return lookup(name);
    }

    public NameParser getNameParser(String name) throws NamingException {
	return getNameParser(new CompositeName(name));
    }

    public NameParser getNameParser(Name name) throws NamingException {
	try {
	    Name[] nm = parseComponents(name);
	    Name mine = nm[0];
	    Name rest = nm[1];

	    if (rest == null || rest.isEmpty()) {
		// Terminal; just use head
		return getNameParser_internal(mine);
	    } else if (rest.get(0).equals("") && rest.size() == 1) {
		// Terminal NNS
		return getNameParser_nns(mine);
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
	} catch (CannotProceedException e) {
	    Context cctx = NamingManager.getContinuationContext(e);
	    return cctx.getNameParser(e.getRemainingName());
	}
    }

    private NameParser getNameParser_nns(Name name) 
	throws NamingException {
	    processJunction_nns(name);
	    return null;
    }

    private NameParser getNameParser_internal(Name name) 
	throws NamingException {
	// Do lookup to verify name exists
	Object obj = lookup_internal(name);
	if (obj instanceof Context) {
	    ((Context)obj).close();
	}
	return myParser;
    }

    public String composeName(String name, String prefix)
            throws NamingException {
        Name result = composeName(new CompositeName(name),
                                  new CompositeName(prefix));
        return result.toString();
    }

    public Name composeName(Name name, Name prefix) 
	throws NamingException {
	Name result;

	// Both are compound names, compose using compound name rules
	if (!(name instanceof CompositeName) &&
	    !(prefix instanceof CompositeName)) {
	    result = (Name)(prefix.clone());
	    result.addAll(name);
	    return new CompositeName().add(result.toString());
	}

	// Simplistic implementation: do not support federation
	throw new OperationNotSupportedException(
	    "Do not support composing composite names");
    }

    public Object addToEnvironment(String propName, Object propVal)
            throws NamingException {
        if (myEnv == null) {
            myEnv = new Hashtable(5, 0.75f);
        } 
        return myEnv.put(propName, propVal);
    }

    public Object removeFromEnvironment(String propName) 
            throws NamingException {
        if (myEnv == null)
            return null;

        return myEnv.remove(propName);
    }

    public Hashtable getEnvironment() throws NamingException {
        if (myEnv == null) {
            // Must return non-null
            return new Hashtable(3, 0.75f);
        } else {
            return (Hashtable)myEnv.clone();
        }
    }

    public String getNameInNamespace() throws NamingException {
	FedCtx ancestor = parent;

	// No ancestor
	if (ancestor == null) {
	    return "";
	}

	Name name = myParser.parse("");
	name.add(myAtomicName);

	// Get parent's names
	while (ancestor != null && ancestor.myAtomicName != null) {
	    name.add(0, ancestor.myAtomicName);
	    ancestor = ancestor.parent;
	}
	    
        return name.toString();
    }

    public String toString() {
	if (myAtomicName != null) {
	    return myAtomicName;
	} else {
	    return "ROOT CONTEXT";
	}
    }

    public void close() throws NamingException {
    }

    // Class for enumerating name/class pairs
    class ListOfNames implements NamingEnumeration {
        protected Enumeration names;

        ListOfNames (Enumeration names) {
            this.names = names;
        }

        public boolean hasMoreElements() {
	    try {
		return hasMore();
	    } catch (NamingException e) {
		return false;
	    }
        }

        public boolean hasMore() throws NamingException {
            return names.hasMoreElements();
        }

        public Object next() throws NamingException {
            String name = (String)names.nextElement();
            String className = bindings.get(name).getClass().getName();
            return new NameClassPair(name, className);
        }

        public Object nextElement() {
	    try {
		return next();
	    } catch (NamingException e) {
		throw new NoSuchElementException(e.toString());
	    }
        }

        public void close() {
        }
    }

    // Class for enumerating bindings
    class ListOfBindings extends ListOfNames {

        ListOfBindings(Enumeration names) {
	    super(names);
        }

        public Object next() throws NamingException {
            String name = (String)names.nextElement();
	    Object obj = bindings.get(name);

	    try {
		obj = NamingManager.getObjectInstance(obj, 
		    new CompositeName().add(name), FedCtx.this, 
		    FedCtx.this.myEnv);
	    } catch (Exception e) {
		NamingException ne = new NamingException(
		    "getObjectInstance failed");
		ne.setRootCause(e);
		throw ne;
	    }

            return new Binding(name, obj);
        }
    }

    /**
      * Resolves the nns for 'name' when the named context is acting
      * as an intermediate context.
      *
      * For a system that supports only junctions, this would be
      * equilvalent to 
      *		lookup(name, cont);
      * because for junctions, an intermediate slash simply signifies
      * a syntactic separator. 
      *
      * For a system that supports only implicit nns, this would be
      * equivalent to
      * 	lookup_nns(name, cont);
      * because for implicit nns, a slash always signifies the implicit nns,
      * regardless of whether it is intermediate or trailing.
      *
      * By default this method supports junctions, and also allows for an
      * implicit nns to be dynamically determined through the use of the
      * "nns" reference (see processJunction_nns()).
      * Contexts that implement implicit nns directly should provide an
      * appropriate override.
      *
      * A junction, by definition, is a binding of a name in one
      * namespace to an object in another.  The default implementation
      * of this method detects the crossover into another namespace
      * using the following heuristic:  there is a junction when "name"
      * resolves to a context that is not an instance of
      * this.getClass().  Contexts supporting junctions for which this
      * heuristic is inappropriate should override this method.
      */
    protected Object resolveIntermediate_nns(Name name, Name rest)
	throws NamingException {
	    return resolveIntermediate_nns(name, rest, null);
    }

    protected Object resolveIntermediate_nns(Name name, Name rest,
	Name newName)
	throws NamingException {
	CannotProceedException cpe;
	try {
	    final Object obj = lookup_internal(name);

	    if (obj != null && getClass().isInstance(obj)) {
		// If "obj" is in the same type as this object, it must
		// not be a junction. Continue the lookup with "/".

		cpe = fillInCPE(obj, name,
		    ((Name)(NNS_NAME.clone())).addAll(rest));
		cpe.setRemainingNewName(newName);
	    } else if (obj != null && !(obj instanceof Context)) {
		// obj is not even a context, so try to find its nns
		// dynamically by constructing a Reference containing obj.
		RefAddr addr = new RefAddr("nns") {
		    public Object getContent() {
			return obj;
		    }
		};
		Reference ref = new Reference("java.lang.Object", addr);

		// Resolved name has trailing slash to indicate nns
		CompositeName resName = (CompositeName)name.clone();
		resName.add(""); // add trailing slash

		cpe = fillInCPE(ref, resName, rest);
		cpe.setRemainingNewName(newName);

	    } else {
		// Consume "/" and continue
		return obj;
	    }
	} catch (NamingException e) {
	    e.appendRemainingComponent(""); // add nns back
	    throw e;
	}
	throw cpe;
    }


    /**
     * Locates the nns using the default policy.  This policy fully
     * handles junctions, but otherwise throws an exception when an
     * attempt is made to resolve an implicit nns.
     *
     * The default policy is as follows:  If there is a junction in
     * the namespace, then resolve to the junction and continue the
     * operation there (thus deferring to that context to find its own
     * nns).  Otherwise, resolve as far as possible and then throw
     * CannotProceedException with the resolved object being a reference:
     * the address type is "nns", and the address contents is this
     * context.
     *
     * For example, when bind_nns(name, obj, ...) is invoked, the
     * caller is attempting to bind the object "obj" to the nns of
     * "name".  If "name" is a junction, it names an object in another
     * naming system that (presumably) has an nns.  bind_nns() will
     * first resolve "name" to a context and then attempt to continue
     * the bind operation there, (thus binding to the nns of the
     * context named by "name").  If "name" is empty then throw an
     * exception, since this context does not by default support an
     * implicit nns.
     *
     * To implement a context that does support an implicit nns, it is
     * necessary to override this default policy.  This is done by
     * overriding the xxx_nns() methods (which each call this method
     * by default).
     */
    protected void processJunction_nns(Name name) 
	throws NamingException {
	if (name.isEmpty()) {
	    // Construct a new Reference that contains this context.
	    RefAddr addr = new RefAddr("nns") {
		public Object getContent() {
		    return FedCtx.this;
		}
	    };
	    Reference ref = new Reference("java.lang.Object", addr);

	    throw fillInCPE(ref, NNS_NAME, null);
	} else {
	    Object target;

	    // lookup name to continue operation in nns
	    try {
		target = lookup_internal(name);
	    } catch (NamingException e) {
		e.appendRemainingComponent(""); // add nns back
		throw e;
	    }

	    throw fillInCPE(target, name, NNS_NAME);
	}
    }

    protected CannotProceedException fillInCPE(Object resolvedObj, 
	Name altName, Name remainingName) {
	CannotProceedException cpe = new CannotProceedException();

	// Generic stuff
	cpe.setEnvironment(myEnv);
	cpe.setAltNameCtx(this);

	// Specific stuff
	cpe.setResolvedObj(resolvedObj);
	cpe.setAltName(altName);
	cpe.setRemainingName(remainingName);
	return cpe;
    }

    // Returns true if n contains only empty components
    static protected boolean isAllEmpty(Name n) {
	int count = n.size();
	for (int i =0; i < count; i++ ) {
	    if (!n.get(i).equals("")) {
		return false;
	    }
	}
	return true;
    }

    private String getInternalName(Name name) {
	return name.get(0);
    }


    static FedCtx testRoot;
    static {
	try {
	    testRoot = new FedCtx(null);
	    testRoot.bind("a", "A is for Apple");
	    testRoot.bind("b", "B is for Breakfast");
	    testRoot.bind("c", "C is for Cauliflower");

	    Context nextRoot = new HierCtx(null);
	    nextRoot.createSubcontext("one").createSubcontext("two");

	    testRoot.bind("neighbor", nextRoot);
	    testRoot.bind("dynamic", "find me an NNS");

	} catch (NamingException e) {
	}
    }

    public static Context getStaticNamespace(Hashtable env) {
	testRoot.myEnv = env;
	return testRoot;
    }

    public static void main(String[] args) {
	try {
	    Hashtable env = new Hashtable();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "tut.FedCtxFactory");
	    env.put(Context.OBJECT_FACTORIES, "tut.StrFactory");
	    Context ctx = new InitialContext(env);

	    System.out.println(ctx.getNameInNamespace());

	    System.out.println(ctx.lookup(""));
	    System.out.println(ctx.lookup("a"));

	    Object obj = ctx.lookup("neighbor/two.one");
	    System.out.println(obj);
	    System.out.println(((Context)obj).getNameInNamespace());

	    System.out.println(ctx.lookup("dynamic"));
	    System.out.println(ctx.lookup("dynamic/b.a"));

	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }
};

class FedParser implements NameParser {

    private static final Properties syntax = new Properties();
    static {
        syntax.put("jndi.syntax.direction", "flat");
	syntax.put("jndi.syntax.escape", "\\");
	syntax.put("jndi.syntax.beginquote", "'");
    }

    public Name parse(String name) throws NamingException {
        return new CompoundName(name, syntax);
    }
};
