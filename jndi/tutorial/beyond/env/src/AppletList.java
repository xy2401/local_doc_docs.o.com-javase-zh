/* 
 * @(#)AppletList.java	1.1 99/10/29
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
 * Demonstrates how to get environment properties via applet
 * parameters. This applet executes a list() and displays the
 * output in a textarea. All of the configuration information
 * required for JNDI are accessed via the appplet's parameters.
 *
 * Load it by going to http://<server>/appletlist.html
 */
 
import javax.naming.*;
import java.util.Hashtable;
import java.applet.Applet;
import java.awt.TextArea;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AppletList extends Applet {

    /**
     * Initialize the applet.
     */
    public void init() {
	TextArea console = new TextArea("", 10, 40, TextArea.SCROLLBARS_BOTH);
	add(console);

	// Create byte stream for applet to write into
	ByteArrayOutputStream str = new ByteArrayOutputStream();
	PrintStream out = new PrintStream(str);

	// Perform list
	doList(out);
	out.flush();

	// Write output to text area
	console.append(str.toString());
	resize(400, 200);
    }

    public void doList (PrintStream out) {
	String target = "";

	out.println("List Results:");

	try {
	    // Put this applet instance into environment
	    Hashtable env = new Hashtable();
	    env.put(Context.APPLET, this);

	    // Pass environment to initial context constructor
	    Context ctx = new InitialContext(env);

	    // List objects 
	    NamingEnumeration namingEnum = ctx.list(target);
	    while (namingEnum.hasMore()) {
		out.println(namingEnum.next());
	    }
	    ctx.close();

	} catch (NamingException e) {
	    e.printStackTrace(out);
	}

	out.println("DONE");
    }
}
