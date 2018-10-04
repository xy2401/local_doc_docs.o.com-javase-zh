/* 
 * @(#)UseFilter.java	1.1 99/10/29
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
import javax.naming.event.*;

import java.util.Hashtable;

/**
  * Demonstrates how a listener can be registered with a search filter
  *
  * usage: java UseFilter
  */
class UseFilter {
    public static void main(String[] args) {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDItutorial");

	try {
	    // Get event DirContext for registering listener
	    EventDirContext ctx = (EventDirContext)
		(new InitialDirContext(env).lookup("ou=People"));

	    // Create listener
	    NamingListener listener = new SampleNCListener("nc1");

	    // Set up search constraints
	    SearchControls constraints = new SearchControls();
	    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    
	    // Register listener for namespace change events for
	    // entries identified using a search filter.
	    // In this example, register interest in namespace changes to
	    // objects that have the objectclass "javaobject".
	    ctx.addNamingListener("cn=Rosanna Lee", "(objectclass=javaobject)", 
		constraints, listener);

	    // Create a separate to make some changes
	    new Updater(env, "ou=Objects, cn=Rosanna Lee, ou=People").start();

	    // Wait 1 minutes for listener to receive events
	    try {
		Thread.sleep(60000);
	    } catch (InterruptedException e) {
		System.out.println("sleep interrupted");
	    }
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Helper thread that updates the namespace.
     */
    static class Updater extends Thread {
	private Hashtable env;
	private String target;

	Updater(Hashtable env, String target) {
	    super();
	    this.env = (Hashtable)env.clone();
	    this.target = target;
	    setDaemon(true);  // non-user thread
	}

	public void run() {
	    try {
		// Get target context in which to make changes
		Context ctx = (Context)new InitialContext(env).lookup(target);

		// Add an entry to the namespace
		ctx.bind("cn=TestObject", "How are you?");

		// Rename entry 
		ctx.rename("cn=TestObject", "cn=TestingObj");

		// Remove entry
		ctx.unbind("cn=TestingObj");

		// Close context when we're done
		ctx.close();
	    } catch (NamingException e) {
		System.out.println("Updater failed");
		e.printStackTrace(System.out);
	    }
	}
    }
}

