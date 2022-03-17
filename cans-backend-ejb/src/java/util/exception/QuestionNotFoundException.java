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
public class QuestionNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>QuestionNotFoundException</code> without
     * detail message.
     */
    public QuestionNotFoundException() {
    }

    /**
     * Constructs an instance of <code>QuestionNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public QuestionNotFoundException(String msg) {
        super(msg);
    }
}
