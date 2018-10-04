/* 
 * @(#)RemoteObj.java	1.3 99/09/07
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
import java.rmi.*;

/**
  * Demonstrates how to bind a Remote object to a directory.
  * (Use Unbind to remove binding.)
  * RMI Registry not needed for this example.
  * 
  * NOTE: Requires ldapbp.jar and Java 2 Platform, v 1.2 or higher
  *
  * Before running this example, you need to run 
  * # rmic HelloImpl
  *
  * usage: java -Djava.rmi.server.codebase=codebase RemoteObj
  */
class RemoteObj {
    public static void main(String[] args) {
	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDITutorial");

	try {
	    // Create the initial context
	    DirContext ctx = new InitialDirContext(env);

	    // Create object to be bound
	    Hello h = new HelloImpl();
	    
	    // Bind to directory
	    ctx.bind("cn=RemoteHello", h); 

	    // Check that it is bound
	    Hello h2 = (Hello)ctx.lookup("cn=RemoteHello");
	    System.out.println(h2.sayHello());

	    // Close the context when we're done
	    ctx.close();
	} catch (NamingException e) {
	    System.out.println("Operation failed: " + e);
	} catch (RemoteException e) {
	    System.out.println("Operation failed: " + e);
	}
    }
}
