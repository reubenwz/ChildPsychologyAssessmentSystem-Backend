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
public class SubModuleExistsException extends Exception {

    /**
     * Creates a new instance of <code>SubModuleExistsException</code> without
     * detail message.
     */
    public SubModuleExistsException() {
    }

    /**
     * Constructs an instance of <code>SubModuleExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SubModuleExistsException(String msg) {
        super(msg);
    }
}
