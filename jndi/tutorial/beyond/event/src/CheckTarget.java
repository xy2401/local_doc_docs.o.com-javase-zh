/* 
 * @(#)CheckTarget.java	1.1 99/10/29
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
  * Demonstrates how to determine whether a provider supports
  * listener registration for nonexistent targets.
  *
  * usage: java CheckTarget
  */
class CheckTarget {
    public static void main(String[] args) {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDItutorial");

	try {
	    // Get event context for registering listener
	    EventContext ctx = (EventContext)new InitialContext(env).lookup("");

	    // Create listener
	    NamingListener listener = new MyListener();
	    String target = "ou=Objects, cn=Rosanna Lee, ou=People";

	    // Check whether object exists so that we don't wait
	    // forever for nonexistent object
	    if (!ctx.targetMustExist()) {
		// Check that object exists before continuing
		// If lookup fails, exception will be thrown and 
		// we'd skip registration
		ctx.lookup(target);
	    }

	    // Register listener
	    ctx.addNamingListener(target, EventContext.ONELEVEL_SCOPE, listener);

	    // Create a separate to make some changes
	    new Updater(env, target).start();

	    // Wait 1 minutes for listener to receive events
	    try {
		Thread.sleep(60000);
	    } catch (InterruptedException e) {
		System.out.println("sleep interrupted");
	    }

	    // Not strictly necessary if we're going to close context anyhow
	    ctx.removeNamingListener(listener);

	    // Close context when we're done
	    ctx.close();

	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }
    /**
     * A NamespaceChangeListener and ObjectChangeListener.
     * Interested in object changes.
     * If removed or renamed, unregister self because it won't
     * get any more notifications.
     */
    static class MyListener 
    implements NamespaceChangeListener, ObjectChangeListener {

	public void objectAdded(NamingEvent evt) {
	    // ignore
	}

	public void objectRemoved(NamingEvent evt) {
	    System.out.println(">>> removed: " + evt.getOldBinding().getName());
	    deregisterSelf(evt.getEventContext());
	}

	public void objectRenamed(NamingEvent evt) {
	    System.out.println(">>> renamed: " + evt.getNewBinding().getName());
	    deregisterSelf(evt.getEventContext());
	}
	
	private void deregisterSelf(EventContext ctx) {
	    System.out.println("Deregistering listener...");
	    synchronized (ctx) {
		try {
		    ctx.removeNamingListener(this);
		} catch (NamingException e) {
		    System.out.println("Listener removal problem: " + e);
		}
	    }
	}

	public void objectChanged(NamingEvent evt) {
	    System.out.println(">>> object changed: " + evt.getNewBinding() +
		" from " + evt.getOldBinding());
	}

	public void namingExceptionThrown(NamingExceptionEvent evt) {
	    System.out.println(">>> got an exception");
	    evt.getException().printStackTrace();
	}
    }

    /**
     * Helper thread that updates objects.
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

		// Add an entry to the namespace
		ctx.bind("cn=TestObject", "How are you?");

		// Update the entry by adding an attribute
		Attributes attrs = 
		    new BasicAttributes("description", "test object");

		ctx.modifyAttributes("cn=TestObject",
		    DirContext.ADD_ATTRIBUTE, attrs);

		// Update the entry by removing an attribute
		ctx.modifyAttributes("cn=TestObject",
		    DirContext.REMOVE_ATTRIBUTE, attrs);

		// Remove entry
		ctx.unbind("cn=TestObject");

		// Close context when we're done
		ctx.close();
	    } catch (NamingException e) {
		System.out.println("Updater failed");
		e.printStackTrace(System.out);
	    }
	}
    }

}
