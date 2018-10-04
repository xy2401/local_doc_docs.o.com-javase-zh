/* 
 * @(#)ChangeLoader.java	1.1 99/10/29
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

/**
 * Demonstrates how to affect where the JNDI loads classes from.
 *
 * java ChangeLoader codebase_url
 */

import javax.naming.*;
import java.util.Hashtable;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;

public class ChangeLoader {

    public static void main(String[] args) throws MalformedURLException {
	if (args.length != 1) {
	    System.err.println("usage: java ChangeLoader codebase_url");
	    System.exit(-1);
	}

	String url = args[0];
	ClassLoader prevCl = Thread.currentThread().getContextClassLoader();

	// Create class loader using given codebase
	// Use prevCl as parent to maintain current visibility
	ClassLoader urlCl = URLClassLoader.newInstance(new URL[]{new URL(url)}, prevCl);

        try {
	    // Save class loader so that we can restore later
            Thread.currentThread().setContextClassLoader(urlCl);

	    // Expect that environment properties are in
	    // application resource file found at "url"
	    Context ctx = new InitialContext();

	    System.out.println(ctx.lookup("tutorial/report.txt"));

	    // Close context when no longer needed
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
        } finally {
            // Restore
            Thread.currentThread().setContextClassLoader(prevCl);
        }
    }
}
