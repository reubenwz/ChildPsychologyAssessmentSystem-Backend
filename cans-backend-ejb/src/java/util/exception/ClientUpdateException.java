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
public class ClientUpdateException extends Exception {

    /**
     * Creates a new instance of <code>ClientUpdateExeception</code> without
     * detail message.
     */
    public ClientUpdateException() {
    }

    /**
     * Constructs an instance of <code>ClientUpdateExeception</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ClientUpdateException(String msg) {
        super(msg);
    }
}
