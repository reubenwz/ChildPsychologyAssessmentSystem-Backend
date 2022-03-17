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
public class ExportDataException extends Exception {

    /**
     * Creates a new instance of <code>ExportDataException</code> without detail
     * message.
     */
    public ExportDataException() {
    }

    /**
     * Constructs an instance of <code>ExportDataException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExportDataException(String msg) {
        super(msg);
    }
}
