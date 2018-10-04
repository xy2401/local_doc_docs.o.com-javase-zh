/* 
 * @(#)Retry.java	1.1 99/09/21
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

/**
 * Demonstrates how to search the directory while manually
 * following referrals, retrying a referral connection if
 * asked to do so.
 *
 * usage: java Retry
 */
class Retry {
    public static void main(String[] args) throws IOException {

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:489/o=JNDITutorial");

	// Set referral property to throw ReferralException
	env.put(Context.REFERRAL, "throw");

	try {
	    // Create initial context
	    DirContext ctx = new InitialDirContext(env);

	    // Set controls for performing subtree search
	    SearchControls ctls = new SearchControls();
	    ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

	    // Do this in a loop because we don't know how
	    // many referrals there will be
	    for (boolean moreReferrals = true; moreReferrals;) {
		try {
		    // Perform search
		    NamingEnumeration answer = ctx.search("", "(objectclass=*)", 
			ctls);

		    // Print the answer
		    while (answer.hasMore()) {
			System.out.println(">>>" + 
			    ((SearchResult)answer.next()).getName());
		    }

		    // search completes with no more referrals
		    moreReferrals = false;

		} catch (ReferralException e) {

		    if (!ask("Follow referral " + e.getReferralInfo())) {
			moreReferrals = e.skipReferral();
		    } else {
			// Get credentials for referral being followed
			getCreds(env);
		    }

		    // Do this in a loop in case getReferralContext()
		    // fails with bad authentication info.
		    while (moreReferrals) {
			try {
			    ctx = (DirContext)e.getReferralContext(env);
			    break;	// success: got context
			} catch (AuthenticationException ne) {
			    if (ask("Authentication failed. Retry")) {
				getCreds(env);
				e.retryReferral();
			    } else {
				// give up and go on to next referral
				moreReferrals = e.skipReferral(); 
			    }
			} catch (NamingException ne) {
			    System.out.println("Referral failed: " + ne);
			    // give up and go on to next referral
			    moreReferrals = e.skipReferral(); 
			}
		    }
		}
	    }

	    // Close the context when we're done
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }

    /**
      * Ask user a Yes/No question.
      */
    private static boolean ask(String prompt) throws IOException {
	return getInput(prompt + "? [y/n] ").equalsIgnoreCase("y");
    }

    /**
     * Get principal and password, add to env.
     */
    private static void getCreds(Hashtable env) throws IOException {
	String userid = getInput(Context.SECURITY_PRINCIPAL + ": ");
	String password = getInput(Context.SECURITY_CREDENTIALS + ": ");

	if (userid.length() > 0) {
	    env.put(Context.SECURITY_PRINCIPAL, userid);
	} else {
	    env.remove(Context.SECURITY_PRINCIPAL);
	}

	if (password.length() > 0) {
	    env.put(Context.SECURITY_CREDENTIALS, password);
	} else {
	    env.remove(Context.SECURITY_CREDENTIALS);
	}
    }

    private static String getInput(String prompt) throws IOException {
	System.out.print(prompt);
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	return in.readLine();
    }
}

