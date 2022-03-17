/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Ooi Jun Hao
 */
public class ResponseExistsException extends Exception {

    /**
     * Creates a new instance of <code>ResponseExistsException</code> without
     * detail message.
     */
    public ResponseExistsException() {
    }

    /**
     * Constructs an instance of <code>ResponseExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ResponseExistsException(String msg) {
        super(msg);
    }
}
