/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import javax.ejb.Local;
import util.exception.ExportDataException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface ExportSessionBeanLocal {

    public String exportCleanedData(String startDate, String endDate) throws ExportDataException;
    
}
