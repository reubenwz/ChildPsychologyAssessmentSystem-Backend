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
public class ClientNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ClientNotFoundException</code> without
     * detail message.
     */
    public ClientNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ClientNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ClientNotFoundException(String msg) {
        super(msg);
    }
}
