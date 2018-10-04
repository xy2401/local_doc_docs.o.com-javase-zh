/* 
 * @(#)GetAllComposite.java	1.1 99/10/29
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
import java.util.Enumeration;

/**
  * Demonstrates how to construct a composite name given its
  * string representation, and then use getAll() to enumerate its contents.
  *
  * usage: java GetAllComposite <string composite name>
  */
class GetAllComposite {
    public static void main(String[] args) {
	if (args.length != 1) {
	    System.out.println("usage: java ConstructComposite <string>");
	    System.exit(-1);
	}
	String name = args[0];
	try {
	    CompositeName cn = new CompositeName(name);
	    System.out.println(cn + " has " + cn.size() + " components: ");
	    for (Enumeration all = cn.getAll(); all.hasMoreElements();) {
		System.out.println(all.nextElement());
	    }
	} catch (InvalidNameException e) {
	    System.out.println("Cannot parse name: " + name);
	}
    }
}
