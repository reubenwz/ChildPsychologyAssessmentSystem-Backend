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
public class PasswordChangeRequestNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>PasswordChangeRequestNotFoundException</code> without detail
     * message.
     */
    public PasswordChangeRequestNotFoundException() {
    }

    /**
     * Constructs an instance of
     * <code>PasswordChangeRequestNotFoundException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public PasswordChangeRequestNotFoundException(String msg) {
        super(msg);
    }
}
