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
public class MainQuestionExistsException extends Exception {

    /**
     * Creates a new instance of <code>MainQuestionExistsException</code>
     * without detail message.
     */
    public MainQuestionExistsException() {
    }

    /**
     * Constructs an instance of <code>MainQuestionExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MainQuestionExistsException(String msg) {
        super(msg);
    }
}
