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
public class CaretakerAssessmentExistsException extends Exception {

    /**
     * Creates a new instance of <code>CaretakerAssessmentExistsException</code>
     * without detail message.
     */
    public CaretakerAssessmentExistsException() {
    }

    /**
     * Constructs an instance of <code>CaretakerAssessmentExistsException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CaretakerAssessmentExistsException(String msg) {
        super(msg);
    }
}
