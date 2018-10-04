/* 
 * @(#)SampleMech.java	1.2 01/05/24
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

package examples;

import java.io.UnsupportedEncodingException;
import com.sun.security.sasl.preview.*;

/**
  * Implements the bogus SAMPLE SASL mechanism. 
  */
public class SampleMech implements SaslClient {
    private boolean completed = false;

    public String getMechanismName() {
	return "SAMPLE";
    }

    public boolean hasInitialResponse() {
	return true;
    }

    public byte[] evaluateChallenge(byte[] challengeData) throws SaslException {
	if (completed) {
	    throw new SaslException("Already completed");
	}
	completed = true;
	try {
	    return "sample".getBytes("UTF8");
	} catch (UnsupportedEncodingException e) {
	    throw new SaslException("evaluateChallenge: ", e);
	}
    }

    public boolean isComplete() {
	return completed;
    }

    public byte[] unwrap(byte[] incoming, int offset, int len) 
	throws SaslException {
	throw new SaslException("No negotiated security layer");
    }

    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
	throw new SaslException("No negotiated security layer");
    }

    public String getNegotiatedProperty(String propName) throws SaslException {
	if (propName.equals(Sasl.QOP)) {
	    return "auth";
	}
	return null;
    }

    public void dispose() throws SaslException {
    }
}
