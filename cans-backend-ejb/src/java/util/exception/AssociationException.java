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
public class AssociationException extends Exception {

    /**
     * Creates a new instance of <code>AssociationException</code> without
     * detail message.
     */
    public AssociationException() {
    }

    /**
     * Constructs an instance of <code>AssociationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AssociationException(String msg) {
        super(msg);
    }
}
