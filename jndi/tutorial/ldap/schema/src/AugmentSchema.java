/* 
 * @(#)AugmentSchema.java	1.3 01/05/24
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
  * in the directory, even if some attribute or object class doesn't
  * exist yet in the directory
  *
  * usage: java AugmentSchema name
  */
class AugmentSchema {
    public static void main(String[] args) {
	if (args.length != 1) {
	    System.out.println("Usage: java AugmentSchema name");
	    System.exit(-1);
	}

	// Set up environment for creating initial context
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, 
	    "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDITutorial");

	// Must authenticate as directory administrator in order to update schema
	env.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager");
	env.put(Context.SECURITY_CREDENTIALS, "secret99");
	env.put(Context.SECURITY_AUTHENTICATION, "simple");

	// This is required when using Netscape Directory Server 3.x
	// env.put("com.sun.naming.netscape.schemaBugs", "true");

	try {
	    // Create the initial context
	    DirContext ctx = new InitialDirContext(env);

	    // Get the schema tree root
	    DirContext schema = ctx.getSchema("");

	    String name = args[0];

	    // Ask user what object classes the new entry is to have
	    Vector objectClasses = UseSchema.getObjectClasses();

	    // Verify existence of object class definitions, add if necessary
	    checkDefinition(schema, objectClasses, "ClassDefinition", ocIDs);
 
	    // From object classes, get list of mandatory and optional attributes
	    Vector[] attrNames = UseSchema.getAttributeLists(schema, objectClasses);

	    Attributes attrs = new BasicAttributes(true);

	    // Remove "objectclasses" from mandatory so that we don't ask user again
	    attrNames[0].removeElement("objectclass");
	    Attribute oc = new BasicAttribute("objectclass");
	    for (int i = 0; i < objectClasses.size(); i++) {
		oc.add(objectClasses.elementAt(i));
	    }
	    attrs.put(oc);

	    // Verify existence of attribute definitions, add if necessary
	    checkDefinition(schema, attrNames[0], "AttributeDefinition", attrIDs);
	    checkDefinition(schema, attrNames[1], "AttributeDefinition", attrIDs);

	    // Ask user for values of attributes
	    UseSchema.getAttributeValues(schema, attrNames[0], attrs, 
		"Mandatory Attributes");
	    UseSchema.getAttributeValues(schema, attrNames[1], attrs, 
		"Optional Attribute");

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
     * Check whether schema definition for list of object exist.
     * If not, create and add definition to schema.
     *
     * @param schema Root of schema tree
     * @param names List of names of schema objects
     * @param schemaType Name of schema subtree (e.g. "ClassDefinition")
     * @param schemaAttrIDs List of attribute names that describe schema object of
     *     this type
     */
    static void checkDefinition(DirContext schema, Vector names,
	String schemaType, String[]schemaAttrIDs) 
	throws NamingException, IOException {
	    DirContext root = (DirContext)schema.lookup(schemaType);
	    for (int i = 0; i < names.size(); i++) {
		String name = (String)names.elementAt(i);
		try {
		    // check if definition already exists in schema
		    root.lookup(name);
		} catch (NameNotFoundException e) {
		    // Get definition from user
		    Attributes schemaAttrs = 
			getDefinition(schemaType, name, schemaAttrIDs);

		    // Add definition to schema
		    root.createSubcontext(name, schemaAttrs);
		}
	    }
    }

    /**
     * Ask user for schema definition.
     *
     * @param type Type of schema object being defined
     * @param name Name of schema object being defined
     * @param schemaAttrIDs List of attributes required for schema object defn
     * @return An attribute set containing the schema definition
     */
    static Attributes getDefinition(String type, String name, 
	String[] schemaAttrIDs) throws IOException {
	Attributes store = new BasicAttributes(true); // ignore case
	System.out.println("Getting schema definition for " + type + " " + name);

	for (int i = 0; i < schemaAttrIDs.length; i++) {
	    String val = UseSchema.askUser(schemaAttrIDs[i] + ": ");
	    if (!val.equals("")) {
		StringTokenizer parser = new StringTokenizer(val, ",");
		Attribute attr = new BasicAttribute(schemaAttrIDs[i]);
		while (parser.hasMoreTokens()) {
		    attr.add(parser.nextToken());
		}
		store.put(attr);
	    }
	}
	return store;
    }

    // List of attribute names for an object class definition
    final static String[] ocIDs = {"NUMERICOID", "NAME", "DESC", "OBSOLETE",
				   "SUP", "ABSTRACT", "STRUCTURAL", "AUXILIARY",
				   "MUST", "MAY"};

    // List of attribute names for an attribute type definition
    final static String[] attrIDs = {"NUMERICOID", "NAME", "DESC", "OBSOLETE",
				     "SUP", "EQUALITY", "ORDERING", "SUBSTRING",
				     "SYNTAX", "SINGLE-VALUE", "COLLECTIVE",
				     "NO-USER-MODIFICATION", "USAGE"};
}
