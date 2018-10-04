/* 
 * @(#)RmiiiopObj.java	1.2 99/09/07
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
import javax.rmi.PortableRemoteObject;

/**
  * Demonstrates how to bind a RMI/IIOP object to a directory.
  * (Use Unbind to remove binding.)
  * Neither COS name server nor RMI Registry needed for this example.
  * 
  * NOTE: Requires ldapbp.jar, RMI/IIOP, and either Java 2 Platform, v 1.2
  * or JavaIDL for JDK 1.1. If using Java 2 Platform, v 1.3, do not need
  * separate RMI/IIOP.
  *
  * Before compiling this example, you need to run 
  * # $RMI_IIOP_HOME/bin/rmic -iiop RiHelloImpl
  *
  * usage: java RmiiiopObj
  */
class RmiiiopObj {
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
	    Hello h = new RiHelloImpl();
	    
	    // Bind to directory
	    ctx.bind("cn=RmiiiopHello", h); 

	    // Look up object
	    org.omg.CORBA.Object cobj = (org.omg.CORBA.Object)ctx.lookup(
		"cn=RmiiiopHello");

	    // Narrow to desired type
 	    Hello robj = (Hello)PortableRemoteObject.narrow(
		cobj, Hello.class);

	    // Invoke method
	    System.out.println(robj.sayHello());

	    // Close the context when we're done
	    ctx.close();
	} catch (NamingException e) {
	    System.out.println("Operation failed: " + e);
	} catch (RemoteException e) {
	    System.out.println("Operation failed: " + e);
	}
    }
}
