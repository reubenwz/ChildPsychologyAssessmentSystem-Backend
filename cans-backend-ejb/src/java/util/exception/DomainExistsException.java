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
public class DomainExistsException extends Exception {

    /**
     * Creates a new instance of <code>DomainExistsException</code> without
     * detail message.
     */
    public DomainExistsException() {
    }

    /**
     * Constructs an instance of <code>DomainExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DomainExistsException(String msg) {
        super(msg);
    }
}
