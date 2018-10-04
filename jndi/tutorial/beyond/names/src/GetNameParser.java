/* 
 * @(#)GetNameParser.java	1.1 99/10/29
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
import java.util.Hashtable;

/**
  * Demonstrates how to get a name parser.
  *
  * usage: java GetNameParser
  */
class GetNameParser {
    public static void main(String[] args) {
	try {
	    // Create the initial context
	    Context ctx = new InitialContext();

	    // Get the parser for LDAP
	    NameParser ldapParser = 
		ctx.getNameParser("ldap://localhost:389/o=jnditutorial");

	    // Get the parser for filenames
	    NameParser fsParser = ctx.getNameParser("file:/");

	    Name compoundName;
	    for (int i = 0; i < args.length; i++) {
		System.out.println("Parsing name: " + args[i]);

		System.out.println("LDAP components:");
		// Parse name using LDAP parser
		compoundName = ldapParser.parse(args[i]);

		// List components in name
		for (int j = 0; j < compoundName.size(); j++) {
		    System.out.println(compoundName.get(j));
		}

		System.out.println("Filename components:");
		// Parse name using filename parser
		compoundName = fsParser.parse(args[i]);

		// List components in name
		for (int j = 0; j < compoundName.size(); j++) {
		    System.out.println(compoundName.get(j));
		}
	    }

	    // Close ctx when done
	    ctx.close();
	} catch (NamingException e) {
	    System.out.println(e);
	}
    }
}
