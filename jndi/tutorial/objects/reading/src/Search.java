/* 
 * @(#)Search.java	1.4 99/09/07
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
 * Demonstrates how to perform a search by specifying a search filter
 * and search controls, asking for the object of each entry to be returned.
 *
 * usage: java Search
 */
class Search {
    public static void printSearchEnumeration(NamingEnumeration namingEnum) {
	try {
	    while (namingEnum.hasMore()) {
		SearchResult sr = (SearchResult)namingEnum.next();
		System.out.println(sr.getName() + ": " + 
		    sr.getClassName() + ": " + sr.getObject());
	    }
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDITutorial");

	// So that object factory can be found
	env.put(Context.OBJECT_FACTORIES, "DrinkFactory");

	try {
	    // Create initial context
	    DirContext ctx = new InitialDirContext(env);

	    SearchControls ctls = new SearchControls();
	    ctls.setReturningObjFlag(true);

	    // Specify the search filter to match any object
	    String filter = "(objectclass=*)";

	    // Search for objects using filter
	    NamingEnumeration answer = ctx.search("", filter, ctls);

	    // Print the answer
	    printSearchEnumeration(answer);

	    // Close the context when we're done
	    ctx.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
