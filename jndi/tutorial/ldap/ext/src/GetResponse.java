/* 
 * @(#)GetResponse.java	1.1 99/09/21
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
 * Demonstrates how to retrieve context response controls. Also demonstrates
 * the use of the com.sun.jndi.ldap.ctl.SortControl/SortResponseControl.
 *
 * usage: java GetResponse
 */
class GetResponse {
    public static void main(String[] args) throws IOException {

	// Get key to sort by; use "cn" by default
	String key = args.length == 0? "cn" : args[0];

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

	    // Create critical VLV that returns the first 10 answers
	    VirtualListViewControl vctl = 
		new VirtualListViewControl(0, 10, 0, 5, Control.CRITICAL);
	    SortControl sctl = 
		new SortControl(new String[]{"cn"}, Control.CRITICAL);

	    // Set context's request controls 
	    ctx.setRequestControls(new Control[]{sctl, vctl});

	    // Perform search
	    NamingEnumeration answer = ctx.search("ou=People", "(cn=*)", null);

	    // examine the response controls (if any)
	    printControls("After search", ctx.getResponseControls());

	    // Enumerate answers
	    while (answer.hasMore()) {
		SearchResult si = (SearchResult)answer.next();
		System.out.println(si.getName() + ": " + 
		    si.getAttributes().get(key));

		// examine the response controls (if any)
		if (si instanceof HasControls) {
		    printControls(si.getName(), ((HasControls)si).getControls());
		}
	    }

	    // examine the response controls (if any)
	    printControls("After enumeration", ctx.getResponseControls());

	    // Close when no longer needed
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }

    static void printControls(String msg, Control[] controls) {
	System.out.println("--->" + msg);
	if (controls != null) {
	    for (int i = 0; i < controls.length; i++) {
		if (controls[i] instanceof SortResponseControl) {
		    SortResponseControl src = (SortResponseControl)controls[i];
		    if (src.isSorted()) {
			System.out.println("Sorted-Search completed successfully");
		    } else {
			System.out.println(
		    "Sorted-Search did not complete successfully: error (" +
			    src.getResultCode() + ") on attribute '" + 
			    src.getAttributeID() + "'");
		    }
		} else if (controls[i] instanceof VirtualListViewResponseControl) {

		    int rc;
		    VirtualListViewResponseControl vlvrc =
			(VirtualListViewResponseControl)controls[i];
		    if ((rc = vlvrc.getResultCode()) == 0) {
			System.out.println("Sorted-View completed successfully");
		    } else {
			System.out.println(
			    "Sorted-View did not complete successfully: error " + 
			    rc);
		    }
		} else {
		    Control c = controls[i];
		    System.out.println("Received control: "+ c.getID());
		}
	    }
	} else {
	    System.out.println("No response controls");
	}
    }

}
