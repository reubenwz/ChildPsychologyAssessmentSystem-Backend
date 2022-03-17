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
public class CertificationExistsException extends Exception {

    /**
     * Creates a new instance of <code>CertificationExistsException</code>
     * without detail message.
     */
    public CertificationExistsException() {
    }

    /**
     * Constructs an instance of <code>CertificationExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CertificationExistsException(String msg) {
        super(msg);
    }
}
