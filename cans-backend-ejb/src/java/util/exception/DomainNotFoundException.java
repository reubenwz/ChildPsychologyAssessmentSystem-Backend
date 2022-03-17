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
public class DomainNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>DomainNotFoundException</code> without
     * detail message.
     */
    public DomainNotFoundException() {
    }

    /**
     * Constructs an instance of <code>DomainNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DomainNotFoundException(String msg) {
        super(msg);
    }
}
