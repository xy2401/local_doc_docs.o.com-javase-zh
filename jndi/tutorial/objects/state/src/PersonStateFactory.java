/* 
 * @(#)PersonStateFactory.java	1.1 99/08/13
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
import javax.naming.spi.DirStateFactory;
import java.util.Hashtable;

/**
 * This is a state factory that when given a Person object,
 * returns an Attributes representing the object.
 */
public class PersonStateFactory implements DirStateFactory {
    public PersonStateFactory() {
    }

    // DirStateFactory version
    public DirStateFactory.Result getStateToBind(
	Object obj, Name name, Context ctx, Hashtable env, Attributes inAttrs)
	throws NamingException {

	    // Only interested in Person objects
	if (obj instanceof Person) {

	    Attributes outAttrs;
	    if (inAttrs == null) {
		outAttrs = new BasicAttributes(true);
	    } else {
		outAttrs = (Attributes)inAttrs.clone();
	    }

	    // Set up object class
	    if (outAttrs.get("objectclass") == null) {
		BasicAttribute oc = new BasicAttribute("objectclass", "person");
		oc.add("top");
		outAttrs.put(oc);
	    }

	    Person per = (Person)obj;
	    // mandatory attributes
	    if (per.surname != null) {
		outAttrs.put("sn", per.surname);
	    } else {
		throw new SchemaViolationException("Person must have surname");
	    }
	    if (per.commonName != null) {
		outAttrs.put("cn", per.commonName);
	    } else {
		throw new SchemaViolationException("Person must have common name");
	    }

	    // optional attributes
	    if (per.passwd != null) {
		outAttrs.put("userPassword", per.passwd);
	    }
	    if (per.phone != null) {
		outAttrs.put("telephoneNumber", per.phone);
	    }
	    if (per.seeAlso != null) {
		outAttrs.put("seeAlso", per.seeAlso);
	    }
	    if (per.desc != null) {
		outAttrs.put("description", per.desc);
	    }

	    //System.out.println("state factory: " + outAttrs);
	    return new DirStateFactory.Result(null, outAttrs);
	}
	return null;
    }

    // StateFactory version
    public Object getStateToBind(
	Object obj, Name name, Context ctx, Hashtable env)
	throws NamingException {

	    // non-Attributes version not relevant here
	    return null;
    }
}
