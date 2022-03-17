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
public class CaretakerExistsException extends Exception {

    /**
     * Creates a new instance of <code>CaretakerExistsException</code> without
     * detail message.
     */
    public CaretakerExistsException() {
    }

    /**
     * Constructs an instance of <code>CaretakerExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CaretakerExistsException(String msg) {
        super(msg);
    }
}
