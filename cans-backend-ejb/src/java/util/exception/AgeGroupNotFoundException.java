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
public class AgeGroupNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>AgeGroupNotFoundException</code> without
     * detail message.
     */
    public AgeGroupNotFoundException() {
    }

    /**
     * Constructs an instance of <code>AgeGroupNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AgeGroupNotFoundException(String msg) {
        super(msg);
    }
}
