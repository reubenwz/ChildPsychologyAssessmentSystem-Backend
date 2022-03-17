/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Ong Bik Jeun
 */
public class CertificationNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CertificationNotFoundException</code>
     * without detail message.
     */
    public CertificationNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CertificationNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CertificationNotFoundException(String msg) {
        super(msg);
    }
}
