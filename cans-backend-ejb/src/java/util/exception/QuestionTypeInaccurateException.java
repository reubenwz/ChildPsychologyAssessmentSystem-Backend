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
public class QuestionTypeInaccurateException extends Exception {

    /**
     * Creates a new instance of <code>QuestionTypeInaccurateException</code>
     * without detail message.
     */
    public QuestionTypeInaccurateException() {
    }

    /**
     * Constructs an instance of <code>QuestionTypeInaccurateException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public QuestionTypeInaccurateException(String msg) {
        super(msg);
    }
}
