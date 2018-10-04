/* 
 * @(#)UseSchema.java	1.3 99/09/21
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
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
  * Demonstrates how to use information in the schema to create an entry
  * in the directory
  *
  * usage: java UseSchema name
  */
class UseSchema {
    public static void main(String[] args) {
	if (args.length != 1) {
	    System.out.println("Usage: java UseSchema name");
	    System.exit(-1);
	}

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDITutorial");

	try {
	    // Create the initial context
	    DirContext ctx = new InitialDirContext(env);

	    // Get the schema tree root
	    DirContext schema = ctx.getSchema("");

	    String name = args[0];

	    // Ask user what object classes the new entry is to have
	    Vector objectClasses = getObjectClasses();
 
	    // From object classes, get list of mandatory and optional attributes
	    Vector[] attrNames = getAttributeLists(schema, objectClasses);

	    Attributes attrs = new BasicAttributes(true);

	    // Remove "objectclasses" from mandatory so that we don't ask user again
	    attrNames[0].removeElement("objectclass");
	    Attribute oc = new BasicAttribute("objectclass");
	    for (int i = 0; i < objectClasses.size(); i++) {
		oc.add(objectClasses.elementAt(i));
	    }
	    attrs.put(oc);

	    // Ask user for values of attributes
	    getAttributeValues(schema, attrNames[0], attrs, "Mandatory Attributes");
	    getAttributeValues(schema, attrNames[1], attrs, "Optional Attribute");

	    System.out.println(attrs);

	    // Create entry
	    ctx.createSubcontext(name, attrs);

	    // Close the context when we're done
	    ctx.close();
	} catch (NamingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Ask user for list of object classes that the new entry is to have.
     * @return A vector of String containing names of object classes
     */
    static Vector getObjectClasses() throws IOException {
	Vector ocs = new Vector();
	while (true) {
	    String oc = askUser("Enter Object Class: ");
	    if ("".equals(oc)) {
		break;
	    }
	    ocs.addElement(oc);
	}
	return ocs;
    }

    /**
     * Generate list of manadatory and optional attributes and store them
     * into two vectors 'mandatory' and 'optional'.
     * Return these vectors when we're done
     *
     * @param schema The schema tree for looking up object class defintions
     * @param objectClasses The list of object classes for which to get attributes
     * @return an array containing two vectors: mandatory and optional attributes
     */
    static Vector[] getAttributeLists(DirContext schema, Vector objectClasses) 
    throws NamingException {
        Vector mandatory = new Vector();
	Vector optional = new Vector();

	for (int i = 0; i < objectClasses.size(); i++) {
	    String oc = (String)objectClasses.elementAt(i);
	    Attributes ocAttrs = 
		schema.getAttributes("ClassDefinition/" + oc);
	    Attribute must = ocAttrs.get("MUST");
	    Attribute may = ocAttrs.get("MAY");

	    if (must != null) {
		addAttrNameToList(mandatory, must.getAll());
	    }
	    if (may != null) {
		addAttrNameToList(optional, may.getAll());
	    }
	}
	return new Vector[] {mandatory, optional};
    }	

    /**
     * Add attribute names to a vector, avoid duplicates.
     *
     * @param store	Vector to store attribute names
     * @param vals      Enumeration of attribute names
     */
    static void addAttrNameToList(Vector store, NamingEnumeration vals) 
	throws NamingException {
	    while (vals.hasMore()) {
		Object val = vals.next();
		if (!store.contains(val)) {
		    store.addElement(val);
		}
	    }
    }

    /**
     * Ask user for values of attribute values.
     * @param schema Schema tree to use for looking up attribute definitions
     * @param attrNames List of attribute string names
     * @param store Place to store attributes created using user input
     * @param prompt Information to use in prompt to user
     */
    static void getAttributeValues(DirContext schema, Vector attrNames, 
	Attributes store, String prompt) throws NamingException, IOException {
	System.out.println("Enter values for " + prompt + ":");

	for (int i = 0; i < attrNames.size(); i++) {
	    String name = (String)attrNames.elementAt(i);

	    Attributes attrSchema = 
		schema.getAttributes("AttributeDefinition/" + name);
	    Attribute syntax = attrSchema.get("SYNTAX");

	    // Use schema to construct a more informative prompt
	    // Perhaps the OID of it syntax is not too informative :-) 
            // but you get the idea

	    String msg;
	    if (syntax != null) {
		msg = name + "(" + syntax.get() + ")";
	    } else {
		msg = name;
	    }

	    String val = askUser(msg + ": ");

	    // %%% should be more fussy about mandatory attributes
	    if (!val.equals("")) {
		StringTokenizer parser = new StringTokenizer(val, ",");
		Attribute attr = new BasicAttribute(name);
		while (parser.hasMoreTokens()) {
		    attr.add(parser.nextToken());
		}
		store.put(attr);
	    }
	}
    }

    /**
      * Displays msg information and read input from user
      * @param prompt String to display to user
      * @return A non-null string entered by user
      */
    static String askUser(String prompt) throws IOException {
	System.out.print(prompt);

	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	return in.readLine();
    }
}
