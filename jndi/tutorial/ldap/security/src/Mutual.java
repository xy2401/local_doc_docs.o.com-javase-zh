/* 
 * @(#)Mutual.java	1.1 01/05/24
 * 
 * Copyright 2001 Microsystems, Inc. All Rights
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
import javax.security.auth.login.*;
import javax.security.auth.Subject;

import java.util.Hashtable;

/**
 * Demonstrates how to create an initial context to an LDAP server
 * using "GSSAPI" SASL authentication (Kerberos v5).
 * Requires J2SE 1.4, or JNDI 1.2 with ldapbp.jar, JAAS, JCE, an RFC 2853
 * compliant implementation of J-GSS and a Kerberos v5 implementation.
 * Uses SampleCallbackHandler.
 *
 * usage: java 
 *    -Djava.security.auth.login.config=gssapi_jaas.conf \
 *    -Djava.security.krb5.conf=krb5.conf \
 *      Mutual
 * 
 * The first property indicates which JAAS login module the application needs
 * to use; the second property is for configuration of the Kerberos subsystem.
 *
 */
class Mutual {

    public static void main(String[] args) {

	// 1. Log in (to Kerberos)
	LoginContext lc = null;
	try {
	    lc = new LoginContext(Mutual.class.getName(),
		new SampleCallbackHandler());

	    // Attempt authentication
	    // You might want to do this in a "for" loop to give
	    // user more than one chance to enter correct username/password
	    lc.login();

	} catch (LoginException le) {
	    System.err.println("Authentication attempt failed" + le);
	    System.exit(-1);
	}

	// 2. Perform JNDI work as logged in subject
	Subject.doAs(lc.getSubject(), new JndiAction(args));
    }
}


/**
 * The application must supply a PrivilegedAction that is to be run
 * inside a Subject.doAs() or Subject.doAsPrivileged().
 */
class JndiAction implements java.security.PrivilegedAction {
    private String[] args;

    public JndiAction(String[] origArgs) {
	this.args = (String[])origArgs.clone();
    }

    public Object run() {
	performJndiOperation(args);
	return null;
    }

    private static void performJndiOperation(String[] args) {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);

	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");

	// Must use fully qualified hostname
	env.put(Context.PROVIDER_URL, 
	    "ldap://ldap.jnditutorial.org:389/o=JndiTutorial");
    
	// Request the use of the "GSSAPI" SASL mechanism
	// Authenticate by using already established Kerberos credentials
	env.put(Context.SECURITY_AUTHENTICATION, "GSSAPI");

	// Request mutual authentication
	env.put("javax.security.sasl.server.authentication", "true");

	try {
	    /* Create initial context */
	    DirContext ctx = new InitialDirContext(env);

	    System.out.println(ctx.getAttributes(""));

	    // do something useful with ctx

	    // Close the context when we're done
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }
}
