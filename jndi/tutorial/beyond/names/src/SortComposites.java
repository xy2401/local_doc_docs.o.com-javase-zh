/* 
 * @(#)SortComposites.java	1.1 99/10/29
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
  * Demonstrates how to sort a list of composite names.
  *
  * usage: java SortComposites [<name>]*
  */
class SortComposites {
    public static void main(String[] args) {
	if (args.length == 0) {
	    System.out.println("usage: java SortComposites [<names>]*");
	    System.exit(-1);
	}
	CompositeName[] names = new CompositeName[args.length];
	try {
	    for (int i = 0; i < names.length; i++) {
		names[i] = new CompositeName(args[i]);
	    }

	    sort(names);

	    for (int i = 0; i < names.length; i++) {
		System.out.println(names[i]);
	    }
	} catch (InvalidNameException e) {
	    System.out.println(e);
	}
    }

    /**
     * Use bubble sort.
     */
    private static void sort(CompositeName[] names) {
	int bound = names.length-1;
	CompositeName tmp;

	while (true) {
	    int t = -1;
	    for (int j=0; j < bound; j++) {
		int c = names[j].compareTo(names[j+1]);
		if (c > 0) {
		    tmp = names[j];
		    names[j] = names[j+1];
		    names[j+1] = tmp;
		    t = j;
		}
	    }
	    if (t == -1) break;
	    bound = t;
	}
    }
}
