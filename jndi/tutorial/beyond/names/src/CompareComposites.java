/* 
 * @(#)CompareComposites.java	1.1 99/10/29
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

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;

/**
  * Demonstrates the use of some comparison methods in the CompositeName class.
  *
  * usage: java CompareComposites
  */
class CompareComposites {
    public static void main(String[] args) {
	try {
	    CompositeName one = new CompositeName("cn=fs/o=JNDITutorial/tmp/a/b/c");
	    CompositeName two = new CompositeName("tmp/a/b/c");
	    CompositeName three = new CompositeName("cn=fs/o=JNDITutorial");
	    CompositeName four = new CompositeName();

	    System.out.println(one.equals(two)); 	// false
	    System.out.println(one.startsWith(three));  // true
	    System.out.println(one.endsWith(two));      // true
	    System.out.println(one.startsWith(four));   // true
	    System.out.println(one.endsWith(four));     // true
	    System.out.println(one.endsWith(three));    // false
	    System.out.println(one.isEmpty());		// false
	    System.out.println(four.isEmpty());		// true
	    System.out.println(four.size() == 0);	// true

	} catch (InvalidNameException e) {
	    System.out.println(e);
	}
    }
}
