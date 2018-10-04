/* 
 * @(#)UpdateEnv.java	1.1 99/10/29
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
 * Demonstrates how to add/replace a context's environment property.
 * Usage: java AddEnv
 */
 
import javax.naming.*;
import java.util.Hashtable;

class UpdateEnv {
    public static void main(String[] args) {
      try {
	  // Initial Environment with various properties
	  Hashtable env = new Hashtable();
	  env.put(Context.INITIAL_CONTEXT_FACTORY, 
	      "com.sun.jndi.fscontext.FSContextFactory");
	  env.put(Context.PROVIDER_URL, "file:/");
	  env.put(Context.OBJECT_FACTORIES, "foo.bar.ObjFactory");
	  env.put("foo", "bar");

	  // Call constructor
	  Context ctx = new InitialContext(env);

	  // Get child context
	  Context child = (Context)ctx.lookup("tmp");

	  // See what properties initial context has
	  System.out.println(ctx.getEnvironment());

	  // Replace foo in parent
	  ctx.addToEnvironment("foo", "baz");
	  
	  // Add a new property to parent
	  ctx.addToEnvironment("com.wiz.jndi.wizProp", "wizards");

	  // Remove an attribute from parent
	  ctx.removeFromEnvironment(Context.OBJECT_FACTORIES);

	  // Remote property from child
	  child.removeFromEnvironment(Context.PROVIDER_URL);

	  // See what environment properties we have after updates
	  System.out.println(">>>>> Parent context: ");
	  System.out.println(ctx.getEnvironment());

	  // See what environment properties child has after updates
	  System.out.println(">>>>> Child context: ");
	  System.out.println(child.getEnvironment());

	  // Close context when we're done
	  ctx.close();
      } catch (NamingException e) {
	  e.printStackTrace();
      }
    }
}
