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
public class SubModuleNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>SubModuleNotFoundException</code> without
     * detail message.
     */
    public SubModuleNotFoundException() {
    }

    /**
     * Constructs an instance of <code>SubModuleNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public SubModuleNotFoundException(String msg) {
        super(msg);
    }
}
