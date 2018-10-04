/* 
 * @(#)SampleResponseControlFactory.java	1.1 99/09/21
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

import javax.naming.ldap.Control;
import javax.naming.ldap.ControlFactory;
import javax.naming.NamingException;

/**
 * Sample response control factory for demonstration purposes.
 */
public  class SampleResponseControlFactory extends ControlFactory {

    /**
     * Public no-arg constructor required.
     */
    public SampleResponseControlFactory() {
    }

    public Control getControlInstance(Control ctl) throws NamingException {
	String id = ctl.getID();

	// See if it's one of ours
	if (id.equals(SampleResponseControl.OID)) {
	    return new SampleResponseControl(id, ctl.isCritical(),
		ctl.getEncodedValue());
	} 

	// Not one of ours; return null and let someone else try
	return null;
    }

    public static class SampleResponseControl implements Control {
	private boolean criticality;
	private byte[] bytes;
	private int decodedValue = 42;

	static final String OID = "1.3.6.1.4.1.42.2.27.4.2.3.1.1.9"; // bogus OID

	public SampleResponseControl(String id, boolean crit, byte[]ber) {
	    this.criticality = crit;
	    this.bytes = (byte[])ber.clone();
	    // this.decodedValue = decode ber
	}

	// Type-friendly access method
	public int getDecodedValue() {
	    // ...
	    return decodedValue;
	}

	// Methods from Control interface
	public boolean isCritical() {
	    return criticality;
	}

	public String getID() {
	    return OID;
	}

	public byte[] getEncodedValue() {
	    return bytes;
	}
    }
}
