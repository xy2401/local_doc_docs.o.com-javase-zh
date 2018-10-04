/* 
 * @(#)IsRelative.java	1.2 00/04/28
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
import java.io.File;
import java.util.Hashtable;

/**
  * Demonstrates how to use NameClassPair.isRelative() and URL strings
  * that NameClassPair returns.
  *
  * usage: java IsRelative
  */
class IsRelative {
    public static void main(String[] args) {

	try {
	    // Set up environment for creating initial context
	    Hashtable env = new Hashtable(11);
	    env.put(Context.INITIAL_CONTEXT_FACTORY, 
		"com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(Context.PROVIDER_URL, "ldap://localhost:489/o=JNDItutorial");

	    // Enable referrals so that we get some nonrelative names
	    env.put(Context.REFERRAL, "follow");

	    // Create the initial context
	    DirContext initCtx = new InitialDirContext(env);

	    // Get the target context
	    DirContext targetCtx = (DirContext)initCtx.lookup("ou=All");

	    SearchControls constraints = new SearchControls();
	    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

	    // Perform search on target context
	    NamingEnumeration namingEnum = targetCtx.search("", "(cn=S*)", constraints);
	    Attributes attrs;
	    NameClassPair item;
	    String[] attrIds = new String[]{"telephonenumber"};
	    
	    // For each answer found, get its telephonenumber attribute
	    // If relative, resolve relative to target context
	    // If not relative, resolve relative to initial context
	    while (namingEnum.hasMore()) {
		item = (NameClassPair)namingEnum.next();
		System.out.println(">>>>>" + item.getName() + " ");
		if (item.isRelative()) {
		    attrs = targetCtx.getAttributes(item.getName(), attrIds);
		} else {
		    attrs = initCtx.getAttributes(item.getName(), attrIds);
		}
		System.out.println(attrs);
	    }
	    
	    // Close when done
	    initCtx.close();
	    targetCtx.close();
	} catch (NamingException e) {
	    System.out.println("Lookup failed: " + e);
	}
    }
}
