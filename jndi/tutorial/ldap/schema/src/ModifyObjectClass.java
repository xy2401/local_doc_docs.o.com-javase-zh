/* 
 * @(#)ModifyObjectClass.java	1.1 01/05/24
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
  * Demonstrates how to modify an existing object class in the schema.
  *
  * usage: java ModifyObjectClass
  */
class ModifyObjectClass {
    public static void main(String[] args) {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDITutorial");

	// Must authenticate as directory administrator in order to update schema
	env.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager");
	env.put(Context.SECURITY_CREDENTIALS, "secret99");
	env.put(Context.SECURITY_AUTHENTICATION, "simple");

	// This is required when using Netscape Directory Server 3.x
	// env.put("com.sun.naming.netscape.schemaBugs", "true");
	
	try {
	    // Specify new MAY attribute for schema object
	    Attribute may = new BasicAttribute("MAY", "description");
	    Attributes attrs = new BasicAttributes(false);
	    attrs.put(may);

	    // Create the initial context
	    DirContext ctx = new InitialDirContext(env);

	    // Get the schema tree root
	    DirContext schema = ctx.getSchema("");

	    // Modify schema object for "fooObjectClass"
    	    schema.modifyAttributes("ClassDefinition/fooObjectClass", 
		DirContext.ADD_ATTRIBUTE, attrs);

	    // Get new initial context to avoid seeing cached data
	    ctx.close();
	    ctx = new InitialDirContext(env);
	    schema = ctx.getSchema("");

	    // See updated schema object
	    System.out.println(
		schema.getAttributes("ClassDefinition/fooObjectClass"));

	    // Close the context when we're done
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }
}
