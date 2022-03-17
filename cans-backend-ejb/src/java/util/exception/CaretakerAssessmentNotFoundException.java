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
public class CaretakerAssessmentNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>CaretakerAssessmentNotFoundException</code> without detail message.
     */
    public CaretakerAssessmentNotFoundException() {
    }

    /**
     * Constructs an instance of
     * <code>CaretakerAssessmentNotFoundException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public CaretakerAssessmentNotFoundException(String msg) {
        super(msg);
    }
}
