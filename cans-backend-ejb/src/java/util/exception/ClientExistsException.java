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
public class ClientExistsException extends Exception {

    /**
     * Creates a new instance of <code>ClientExistsException</code> without
     * detail message.
     */
    public ClientExistsException() {
    }

    /**
     * Constructs an instance of <code>ClientExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ClientExistsException(String msg) {
        super(msg);
    }
}
