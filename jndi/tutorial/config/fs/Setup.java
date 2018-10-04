/* 
 * @(#)Setup.java	1.3 01/05/24
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

import java.io.*;

public class Setup {

    public static void main(String[] args) {
	if (args.length == 0) {
	    System.err.println("Usage:  java Setup <directory> [cleanup]");
	    System.exit(-1);
	}

	File root = new File(args[0]);

	if (args.length > 1 && args[1].equals("cleanup")) {
	    cleanup(root);
	} else {
	    if (!root.exists()) {
		root.mkdirs();
	    } else if (root.isFile()) {
		System.err.println("Usage: java Setup <directory> [cleanup]");
		System.exit(-1);
	    }
	    create(root);
	}
    }
    
    static void create(File root) {
	// Create Directories
	File cur;
	for (int i = 0; i < dirs.length; i++) {
	    cur = new File(root, dirs[i]);
	    cur.mkdir();
	}

	// Create Files
	for (int i = 0; i < files.length; i++) {
	    cur = new File(root, files[i]);

	    try {
		FileWriter f = new FileWriter(cur);
		f.write("JNDI Tutorial");
		f.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    static void cleanup(File root) {
	File cur;

	// Delete Files
	for (int i = files.length-1; i >= 0; i--) {
	    cur = new File(root, files[i]);
	    System.out.println("deleting " + cur);
	    cur.delete();
	}

	// Delete directories
	for (int i = dirs.length-1; i >= 0; i--) {
	    cur = new File(root, dirs[i]);
	    System.out.println("deleting " + cur);
	    cur.delete();
	}

	System.out.println("deleting " + root);
	root.delete();
    }

    static final String[] dirs = new String[] {
	"awt", 
	    "awt" + File.separator + "accessibility",
	    "awt" + File.separator + "color",
	    "awt" + File.separator + "datatransfer",
	    "awt" + File.separator + "dnd",
	    "awt" + File.separator + "dnd" + File.separator + "peer",
	    "awt" + File.separator + "event",
	    "awt" + File.separator + "font",
	    "awt" + File.separator + "geom",
	    "awt" + File.separator + "im",
	    "awt" + File.separator + "image",
	    "awt" + File.separator + "image" + File.separator + "codec",
	    "awt" + File.separator + "image" + File.separator + "renderable",
	    "awt" + File.separator + "peer",
	    "awt" + File.separator + "print",
	    "awt" + File.separator + "swing",
	    "awt" + File.separator + "swing" + File.separator + "border",
	    "awt" + File.separator + "swing" + File.separator + "event",
	    "awt" + File.separator + "swing" + File.separator + "plaf",
	    "awt" + File.separator + "swing" + File.separator + "plaf" + File.separator + "basic",
	    "awt" + File.separator + "swing" + File.separator + "plaf" + File.separator + "metal",
	    "awt" + File.separator + "swing" + File.separator + "preview",
	    "awt" + File.separator + "swing" + File.separator + "table",
	    "awt" + File.separator + "swing" + File.separator + "text",
	    "awt" + File.separator + "swing" + File.separator + "text" + File.separator + "html",
	    "awt" + File.separator + "swing" + File.separator + "text" + File.separator + "rtf",
	    "awt" + File.separator + "swing" + File.separator + "tree",
	    "awt" + File.separator + "swing" + File.separator + "undo"};

    static final String[] files = new String[] {
	"awt" + File.separator + "im" + File.separator + "InputContext.java",
	    "awt" + File.separator + "im" + File.separator + "InputMethodHighlight.java",
	    "report.txt"};

}

