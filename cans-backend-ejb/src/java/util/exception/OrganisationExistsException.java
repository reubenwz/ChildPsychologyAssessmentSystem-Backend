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
public class OrganisationExistsException extends Exception {

    /**
     * Creates a new instance of <code>OrganisationExistsException</code>
     * without detail message.
     */
    public OrganisationExistsException() {
    }

    /**
     * Constructs an instance of <code>OrganisationExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public OrganisationExistsException(String msg) {
        super(msg);
    }
}
