/* 
 * @(#)Person.java	1.1 99/08/13
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
  * This class is used by the custom state/object factories example.
  * It is represents a Person object, which is represented in
  * an LDAP directory using the following schema:
  * ( 2.5.6.6 NAME 'person' SUP top STRUCTURAL MUST ( sn $ cn )
  *   MAY ( userPassword $ telephoneNumber $ seeAlso $ description ) )
  */
public class Person {
    public String surname;
    public String commonName;
    public String passwd;
    public String phone;
    public String seeAlso;
    public String desc;

    public Person(String sn, String cn) {
	this(sn, cn, null, null, null, null);
    }
    
    public Person(String sn, String cn, String pw, String ph, 
	String see, String d) {
	surname = sn;
	commonName = cn;
	passwd = pw;
	phone = ph;
	seeAlso = see;
	desc = d;
    }

    public String toString() {
	return "My name is " + surname + ", " + commonName + ".";
    }
}
