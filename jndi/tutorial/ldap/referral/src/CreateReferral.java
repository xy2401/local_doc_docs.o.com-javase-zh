/* 
 * @(#)CreateReferral.java	1.3 99/09/21
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
  * Demonstrates how to create a referral entry.
  *
  * usage: java CreateReferral
  */
class CreateReferral {
    public static void main(String[] args) {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:489/o=JNDITutorial");

	// Set referral property; optional because "ignore" is the default
	env.put(Context.REFERRAL, "ignore");

	// Uncomment this line when using 1.0.1 
	// env.put("java.naming.ldap.control.manageReferral", "true");

	try {
	    // Create the initial context
	    DirContext ctx = new InitialDirContext(env);

	    // The object classes
	    Attribute objclass = new BasicAttribute("objectclass");
	    objclass.add("top");
	    objclass.add("referral");
	    objclass.add("extensibleObject"); // so that we can use cn as name

	    // The referral itself
	    Attribute ref = new BasicAttribute("ref",
		"ldap://locahost:389/cn=J.%20Duke,ou=NewHires,o=JNDITutorial");

	    // The name
	    Attribute cn = new BasicAttribute("cn", "NewReferral");

	    // Create attributes to be associated with the new context
	    Attributes attrs = new BasicAttributes(true); // case-ignore
	    attrs.put(objclass);
	    attrs.put(ref);
	    attrs.put(cn);

	    // Create the context
	    Context result = ctx.createSubcontext("cn=NewReferral", attrs);

	    // Close the contexts when we're done
	    result.close();
	    ctx.close();
	} catch (NamingException e) {
	    System.out.println("Create failed: " + e);
	}
    }
}
