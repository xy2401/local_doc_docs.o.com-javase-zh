/* 
 * "@(#)StartTlsSimple.java	1.1	01/05/24 SMI"
 * 
 * Copyright 2001 Sun Microsystems, Inc. All Rights
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
import javax.naming.ldap.*;

import java.util.Hashtable;
import java.io.IOException;

/**
 * Demonstrates how to use the Start TLS extension with simple
 * authentication.
 *
 * To run this example, you need to use the Java 2 SDK, v1.4 and to
 * have installed the server's certificate in your JRE.
 *
 * usage: java StartTlsSimple
 */

public class StartTlsSimple {
    public static void main(String[] args) {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");

	// Must use the name of the server that is found in its certificate
	env.put(Context.PROVIDER_URL, 
	    "ldap://ldap.jnditutorial.org:389/o=JNDITutorial");

	try {
	    // Create initial context
	    LdapContext ctx = new InitialLdapContext(env, null);

	    // Start TLS
	    StartTlsResponse tls =
		(StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
	    tls.negotiate();

	    // Perform simple client authentication
	    // Authenticate as S. User and password "mysecret"
	    ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
	    ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, 
		"cn=S. User, ou=NewHires, o=JNDITutorial");
	    ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, "mysecret");

	    // ... do something useful with ctx that requires secure connection

	    // Perform read
	    System.out.println(ctx.getAttributes(""));

	    // Close the context when we're done
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
