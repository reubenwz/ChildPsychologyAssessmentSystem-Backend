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
public class AgeGroupExistsException extends Exception {

    /**
     * Creates a new instance of <code>AgeGroupExistsException</code> without
     * detail message.
     */
    public AgeGroupExistsException() {
    }

    /**
     * Constructs an instance of <code>AgeGroupExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AgeGroupExistsException(String msg) {
        super(msg);
    }
}
