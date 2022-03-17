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
public class AssessorDeletionError extends Exception {

    /**
     * Creates a new instance of <code>AssessorDeletionError</code> without
     * detail message.
     */
    public AssessorDeletionError() {
    }

    /**
     * Constructs an instance of <code>AssessorDeletionError</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AssessorDeletionError(String msg) {
        super(msg);
    }
}
