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
public class AssessorExistsException extends Exception {

    /**
     * Creates a new instance of <code>AssessorExistsException</code> without
     * detail message.
     */
    public AssessorExistsException() {
    }

    /**
     * Constructs an instance of <code>AssessorExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AssessorExistsException(String msg) {
        super(msg);
    }
}
