/* 
 * @(#)RegUnsol.java	1.1 99/10/29
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
import javax.naming.ldap.*;
import javax.naming.event.*;

import java.util.Hashtable;

/**
  * Demonstrates how to register a listener for Unsolicited Notifications.
  * In order for the listener to receive a notification, you must
  * prod the directory server to send an Unsolicited Notification.
  * The procedure for doing so is directory-dependent.
  *
  * usage: java RegUnsol
  */
class RegUnsol {
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

	    // Create listener
	    NamingListener listener = new UnsolListener();

	    // Register listener with context (all targets equivalent)
	    ctx.addNamingListener("", EventContext.ONELEVEL_SCOPE, listener);

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
     * A sample UnsolicitedNotificationListener.
     */
    static class UnsolListener implements UnsolicitedNotificationListener {
	public void notificationReceived(UnsolicitedNotificationEvent evt) {
	    System.out.println("received: " + evt);
	}

	public void namingExceptionThrown(NamingExceptionEvent evt) {
	    System.out.println(">>> UnsolListener got an exception");
	    evt.getException().printStackTrace();
	}
    }
}

