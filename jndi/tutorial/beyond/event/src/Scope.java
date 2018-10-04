/* 
 * @(#)Scope.java	1.1 99/10/29
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
  * Demonstrates how to register listeners using different scopes
  * (object, one level, subtree).
  *
  * usage: java Scope
  */
class Scope {
    public static void main(String[] args) {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDItutorial");

	try {
	    // Get event context for registering listener
	    EventContext ctx = (EventContext)
		(new InitialContext(env).lookup("ou=People"));

	    String target = "cn=Rosanna Lee";

	    // Create listeners
	    NamingListener oneListener = new SampleListener("ONELEVEL");
	    NamingListener objListener = new SampleListener("OBJECT");
	    NamingListener subListener = new SampleListener("SUBTREE");

	    // Register listeners using different scopes
	    ctx.addNamingListener(target, EventContext.ONELEVEL_SCOPE, oneListener);
	    ctx.addNamingListener(target, EventContext.OBJECT_SCOPE, objListener);
	    ctx.addNamingListener(target, EventContext.SUBTREE_SCOPE, subListener);

	    // Create a separate to make some changes
	    new Updater(env, "cn=Rosanna Lee, ou=People").start();

	    // Wait 1 minutes for listener to receive events
	    try {
		Thread.sleep(60000);
	    } catch (InterruptedException e) {
		System.out.println("sleep interrupted");
	    }

	    // Not strictly necessary if we're going to close context anyhow
	    ctx.removeNamingListener(objListener);
	    ctx.removeNamingListener(oneListener);
	    ctx.removeNamingListener(subListener);

	    // Close context when we're done
	    ctx.close();

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
		DirContext ctx = (DirContext)
		    new InitialContext(env).lookup(target);

		// ------- changes at the object itself
		Attributes objattrs = 
		    new BasicAttributes("description", "root desc");

		ctx.modifyAttributes("", DirContext.ADD_ATTRIBUTE, objattrs);

		// Update the entry by removing an attribute
		ctx.modifyAttributes("", DirContext.REMOVE_ATTRIBUTE, objattrs);

		// ------- changes 1 level down
		String child = "cn=Child Object";

		// Add an entry to the namespace
		ctx.bind(child, new Integer(10));

		// Remove it
		ctx.unbind(child);

		// ------- changes 2 levels down

		String grandchild = "cn=TestObject, ou=Objects"; 

		// Add an entry to the namespace
		ctx.bind(grandchild, "How are you?");

		// Update the entry by adding an attribute
		Attributes attrs = 
		    new BasicAttributes("description", "test object");

		ctx.modifyAttributes(grandchild, DirContext.ADD_ATTRIBUTE, attrs);

		// Update the entry by removing an attribute
		ctx.modifyAttributes(grandchild,
		    DirContext.REMOVE_ATTRIBUTE, attrs);

		// Remove entry
		ctx.unbind(grandchild);

		// Close context when we're done
		ctx.close();
	    } catch (NamingException e) {
		System.out.println("Updater failed");
		e.printStackTrace(System.out);
	    }
	}
    }
}

