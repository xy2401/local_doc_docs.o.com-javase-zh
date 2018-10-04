/* 
 * @(#)Drink.java	1.3 99/09/07
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

import javax.naming.*;
import javax.naming.directory.*;

import java.util.Hashtable;

/**
  * This class is used by the DirObj example.
  * It is a DirContext class that can be stored by service
  * providers like the LDAP system providers.
  */
public class Drink implements DirContext {
    String type;
    Attributes myAttrs;
    
    public Drink(String d) {
	type = d;
	myAttrs = new BasicAttributes(true);  // case ignore
	Attribute oc = new BasicAttribute("objectclass");
	oc.add("extensibleObject");
	oc.add("top");

	myAttrs.put(oc);
	myAttrs.put("drinkType", d);
    }
    
    public Attributes getAttributes(String name) throws NamingException {
	if (! name.equals("")) {
	    throw new NameNotFoundException();
	}
	return (Attributes)myAttrs.clone();
    }

    public Attributes getAttributes(Name name) throws NamingException {
	return getAttributes(name.toString());
    }

    public Attributes getAttributes(String name, String[] ids) 
	throws NamingException {
	if (! name.equals("")) {
	    throw new NameNotFoundException();
	}

	Attributes answer = new BasicAttributes(true);
	Attribute target;
	for (int i = 0; i < ids.length; i++) {
	    target = myAttrs.get(ids[i]);
	    if (target != null) {
		answer.put(target);
	    }
	}
	return answer;
    }

    public Attributes getAttributes(Name name, String[] ids) 
	throws NamingException {
	return getAttributes(name.toString(), ids);
    }

    public String toString() {
	return type;
    }

// not used for this example

    public Object lookup(Name name) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public Object lookup(String name) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public void bind(Name name, Object obj) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public void bind(String name, Object obj) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public void rebind(Name name, Object obj) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public void rebind(String name, Object obj) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public void unbind(Name name) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public void unbind(String name) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public void rename(Name oldName, Name newName) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public void rename(String oldName, String newName) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration list(Name name) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration list(String name) throws NamingException {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration listBindings(Name name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public NamingEnumeration listBindings(String name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public void destroySubcontext(Name name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public void destroySubcontext(String name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public Context createSubcontext(Name name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public Context createSubcontext(String name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public Object lookupLink(Name name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public Object lookupLink(String name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public NameParser getNameParser(Name name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public NameParser getNameParser(String name) throws NamingException {
	throw new OperationNotSupportedException();
    }
    public String composeName(String name, String prefix)
	    throws NamingException {
	throw new OperationNotSupportedException();
    }

    public Name composeName(Name name, Name prefix)
	    throws NamingException {
	throw new OperationNotSupportedException();
    }

    public Object addToEnvironment(String propName, Object propVal) 
	throws NamingException {
	throw new OperationNotSupportedException();
    }
    public Object removeFromEnvironment(String propName) 
	throws NamingException {
	throw new OperationNotSupportedException();
    }
    public Hashtable getEnvironment() throws NamingException {
	throw new OperationNotSupportedException();
    }
    public void close() throws NamingException {
	throw new OperationNotSupportedException();
    }

// -- DirContext
    public void modifyAttributes(Name name, int mod_op, Attributes attrs)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public void modifyAttributes(String name, int mod_op, Attributes attrs)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public void modifyAttributes(Name name, ModificationItem[] mods)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public void modifyAttributes(String name, ModificationItem[] mods)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public void bind(Name name, Object obj, Attributes attrs)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public void bind(String name, Object obj, Attributes attrs)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public void rebind(Name name, Object obj, Attributes attrs)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public void rebind(String name, Object obj, Attributes attrs)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public DirContext createSubcontext(Name name, Attributes attrs)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public DirContext createSubcontext(String name, Attributes attrs)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public DirContext getSchema(Name name) throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public DirContext getSchema(String name) throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public DirContext getSchemaClassDefinition(Name name)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public DirContext getSchemaClassDefinition(String name)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration search(Name name, 
				    Attributes matchingAttributes,
				    String[] attributesToReturn)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration search(String name, 
				    Attributes matchingAttributes,
				    String[] attributesToReturn)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration search(Name name,
				    Attributes matchingAttributes)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration search(String name,
				    Attributes matchingAttributes)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }
    public NamingEnumeration search(Name name, 
				    String filter,
				    SearchControls cons)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration search(String name, 
				    String filter,
				    SearchControls cons)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration search(Name name,
				    String filterExpr,
				    Object[] filterArgs,
				    SearchControls cons)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public NamingEnumeration search(String name,
				    String filterExpr,
				    Object[] filterArgs,
				    SearchControls cons)
	    throws NamingException  {
	throw new OperationNotSupportedException();
    }

    public String getNameInNamespace() throws NamingException {
	throw new OperationNotSupportedException();
    }
}
