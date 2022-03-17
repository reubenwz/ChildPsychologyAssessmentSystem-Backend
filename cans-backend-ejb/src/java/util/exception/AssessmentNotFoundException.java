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
public class AssessmentNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>AssessmentNotFoundException</code>
     * without detail message.
     */
    public AssessmentNotFoundException() {
    }

    /**
     * Constructs an instance of <code>AssessmentNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public AssessmentNotFoundException(String msg) {
        super(msg);
    }
}
