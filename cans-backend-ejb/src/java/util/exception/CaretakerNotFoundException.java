/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Ziyue
 */
public class CaretakerNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CaretakerNotFoundException</code> without
     * detail message.
     */
    public CaretakerNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CaretakerNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CaretakerNotFoundException(String msg) {
        super(msg);
    }
}
