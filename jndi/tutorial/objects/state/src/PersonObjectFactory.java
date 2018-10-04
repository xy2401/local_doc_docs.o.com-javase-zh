/* 
 * @(#)PersonObjectFactory.java	1.2 00/01/07
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
import javax.naming.spi.DirObjectFactory;
import java.util.Hashtable;

/**
 * This is an object factory that returns a Person object given
 * an Attributes containing a person object class.
 */
public class PersonObjectFactory implements DirObjectFactory {
    public PersonObjectFactory() {
    }

    // DirObjectFactory version
    public Object getObjectInstance(
	Object obj, Name name, Context ctx, Hashtable env, Attributes attrs)
	throws Exception {

	// Only interested in Attributes with person objectclass
	// System.out.println("object factory: " + attrs);
        Attribute oc = (attrs != null ? attrs.get("objectclass") : null);
	if (oc != null && oc.contains("person")) {
	    Attribute attr;
	    String passwd = null;

	    // Extract password
	    attr = attrs.get("userPassword");
	    if (attr != null) {
		passwd = new String((byte[])attr.get());
	    }

	    Person per = new Person(
		(String)attrs.get("sn").get(),
		(String)attrs.get("cn").get(),
		passwd,
		(attr=attrs.get("telephoneNumber")) != null ? (String)attr.get() : null,
		(attr=attrs.get("seealso")) != null ? (String)attr.get() : null,
		(attr=attrs.get("description")) != null ? (String)attr.get() : null);

	    return per;
	}
	return null;
    }

    // ObjectFactory version
    public Object getObjectInstance(
	Object obj, Name name, Context ctx, Hashtable env)
	throws Exception {

	// Don't do anything if we can't see the attributes
	return null;
    }
}
