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
public class LoginTokenNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>LoginTokenNotFoundException</code>
     * without detail message.
     */
    public LoginTokenNotFoundException() {
    }

    /**
     * Constructs an instance of <code>LoginTokenNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public LoginTokenNotFoundException(String msg) {
        super(msg);
    }
}
