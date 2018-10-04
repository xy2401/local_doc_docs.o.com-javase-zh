/* 
 * @(#)SameCtx.java	1.1 99/10/29
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
 * Demonstrates how to use multiple threads on two the same context.
 *
 * java SameCtx
 */

import javax.naming.*;
import java.util.Hashtable;

public class SameCtx {

    public static void main(String[] args) {
	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDItutorial");

        try {
	    // Create the initial context
	    Context ctx = new InitialContext(env);

	    // Create threads
	    Thread thread1 = new ListThread(ctx, "ONE");
	    Thread thread2 = new ListThread(ctx, "TWO");

	    // Let them work
	    thread1.start();
	    thread2.start();

	    // Wait for them to finish
	    try {
		thread1.join();
		thread2.join();
	    } catch (InterruptedException e) {
	    }

	    // Close context when no longer needed
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
        }
    }

    static class ListThread extends Thread {
	private Context ctx;
	private String label;

	ListThread(Context ctx, String label) {
	    this.ctx = ctx;
	    this.label = label;
	}

	public void run() {
	    try {
		// Lock for multithreaded access
		synchronized (ctx) {
		    NamingEnumeration namingEnum = ctx.list("");
		    while (namingEnum.hasMore()) {
			System.out.println(label + ": " + namingEnum.next());
		    }
		}
	    } catch (NamingException e) {
		System.out.println(label + ": " + e);
	    }
	}
    }
}
