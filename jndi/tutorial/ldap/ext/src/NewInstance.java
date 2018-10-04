/* 
 * @(#)NewInstance.java	1.1 99/09/21
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

import java.util.Hashtable;
import java.io.*;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.*;

// Use controls exported by Sun's LDAP provider
import com.sun.jndi.ldap.ctl.*;

/**
 * Demonstrates how to spawn off another context that's initialized
 * with request controls. 
 * Also demonstrates the use of the com.sun.jndi.ldap.ctl.SortControl.
 *
 * usage: java NewInstance
 */
class NewInstance {
    public static void main(String[] args) throws IOException {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDITutorial");

	try {
	    // Create initial context with no connection request controls
	    LdapContext ctx = new InitialLdapContext(env, null);

	    // Create critical Sort that sorts based on CN
	    Control[] ctxCtls = new Control[]{
		new SortControl(new String[]{"cn"}, Control.CRITICAL)
	    };

	    // Create clone with request controls set to ctxCtls
	    LdapContext cloneCtx = ctx.newInstance(ctxCtls);

	    // Perform search using original context
	    NamingEnumeration answer = ctx.search("", "(cn=*)", null);

	    // Enumerate answers (not sorted)
	    System.out.println("-----> Unsorted");
	    while (answer.hasMore()) {
		System.out.println(((SearchResult)answer.next()).getName());
	    }

	    // Perform search using clone context, sort by cn
	    answer = cloneCtx.search("", "(cn=*)", null);

	    System.out.println("-----> Sorted");
	    // Enumerate answers (sorted)
	    while (answer.hasMore()) {
		System.out.println(((SearchResult)answer.next()).getName());
	    }

	    // Close when no longer needed
	    cloneCtx.close();
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }
}
