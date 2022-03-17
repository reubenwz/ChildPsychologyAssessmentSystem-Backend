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
public class AssessmentStatusUpdateException extends Exception {

    /**
     * Creates a new instance of <code>AssessmentStatusUpdateException</code>
     * without detail message.
     */
    public AssessmentStatusUpdateException() {
    }

    /**
     * Constructs an instance of <code>AssessmentStatusUpdateException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public AssessmentStatusUpdateException(String msg) {
        super(msg);
    }
}
