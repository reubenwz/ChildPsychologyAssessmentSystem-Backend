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
public class SubQuestionExistsException extends Exception {

    /**
     * Creates a new instance of <code>SubQuestionExistsException</code> without
     * detail message.
     */
    public SubQuestionExistsException() {
    }

    /**
     * Constructs an instance of <code>SubQuestionExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public SubQuestionExistsException(String msg) {
        super(msg);
    }
}
