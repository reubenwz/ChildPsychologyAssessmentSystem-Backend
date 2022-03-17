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
public class OrganisationNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>OrganisationNotFoundException</code>
     * without detail message.
     */
    public OrganisationNotFoundException() {
    }

    /**
     * Constructs an instance of <code>OrganisationNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public OrganisationNotFoundException(String msg) {
        super(msg);
    }
}
