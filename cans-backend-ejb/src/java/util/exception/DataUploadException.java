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
public class DataUploadException extends Exception {

    /**
     * Creates a new instance of <code>DataUploadException</code> without detail
     * message.
     */
    public DataUploadException() {
    }

    /**
     * Constructs an instance of <code>DataUploadException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DataUploadException(String msg) {
        super(msg);
    }
}
