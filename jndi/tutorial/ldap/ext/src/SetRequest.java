/* 
 * @(#)SetRequest.java	1.1 99/09/21
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
 * Demonstrates how to set context request controls. Also demonstrates
 * the use of the com.sun.jndi.ldap.ctl.SortControl and
 * com.sun.jndi.ldap.ctl.VirtualListViewControl.
 *
 * usage: java SetRequest
 */
class SetRequest {
    public static void main(String[] args) throws IOException {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDITutorial");

	// Required for using VLV control against DS 4.1
	env.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager");
	env.put(Context.SECURITY_CREDENTIALS, "secret99");

	try {
	    // Create initial context with no connection request controls
	    LdapContext ctx = new InitialLdapContext(env, null);

	    // Create critical Sort that sorts based on CN
	    Control[] ctxCtls = new Control[]{
		new SortControl(new String[]{"cn"}, Control.CRITICAL)
	    };

	    // Set context's request controls to be ctxCtls
	    ctx.setRequestControls(ctxCtls);

	    // Perform list, will sort by cn
	    NamingEnumeration answer = ctx.list("");

	    // Enumerate answers
	    System.out.println("---->sort by cn");
	    while (answer.hasMore()) {
		System.out.println(((NameClassPair)answer.next()).getName());
	    }

	    // Perform search, will still sort by cn
	    // because context request controls are still in effect
	    answer = ctx.search("ou=People", "(cn=*)", null);

	    // Enumerate answers
	    System.out.println("---->sort ou=People by cn");
	    while (answer.hasMore()) {
		System.out.println(((SearchResult)answer.next()).getName());
	    }

	    // Context request controls remains set until replaced
	    // Replace controls with Sort+VirtualListView
	    ctxCtls = new Control[]{
		new SortControl(new String[]{"telephonenumber"}, Control.CRITICAL),
		    new VirtualListViewControl(0, 10, 0, 9, Control.CRITICAL)};

	    ctx.setRequestControls(ctxCtls);

	    // Perform search, sort by telephonenumber, return first 10 answers
	    answer = ctx.search("ou=People", "(objectclass=*)", null);

	    // Enumerate answers (first page only)
	    System.out.println("---->sort by telephonenumber, list size = 10");
	    int count = 0;
	    while (answer.hasMore()) {
		System.out.print(count++ + ": ");
		System.out.println(answer.next());
	    }

	    // Close when no longer needed
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }
}
