/* 
 * @(#)ReferralConnect.java	1.2 00/04/28
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

import com.sun.jndi.ldap.ctl.*; 

/**
 * Demonstrates how to set connection controls of a referral context.
 *
 * usage: java ReferralConnect
 */
class ReferralConnect {
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
	    LdapContext ctx = new InitialLdapContext(env, null);

	    // Want to search subtree
	    SearchControls constraints = new SearchControls();
	    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

	    // Do this in a loop because we don't know how
	    // many referrals there will be
	    for (boolean moreReferrals = true; moreReferrals;) {
		try {
		    // Perform search
		    NamingEnumeration answer = ctx.search("", "(objectclass=*)", 
			constraints);

		    // Enumerate answers
		    while (answer.hasMore()) {
			answer.next();
		    }

		    // search completes with no more referrals
		    moreReferrals = false;

		} catch (LdapReferralException e) {
		    System.out.println("Setting controls for " +
			e.getReferralInfo());

		    Control[] connCtls = new Control[]{new SampleRequestControl()};
		    Control[] ctxCtls = new Control[]{
			new SortControl(new String[]{"cn"}, Control.CRITICAL)
		    };

		    // Get referral context using connection controls
		    // when establishing connection using the referral
		    ctx = (LdapContext) e.getReferralContext(env, connCtls);

		    // Set context request controls for referral context
		    ctx.setRequestControls(ctxCtls);
		} 
	    }

	    // Close the context when we're done
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	}
    }
}
