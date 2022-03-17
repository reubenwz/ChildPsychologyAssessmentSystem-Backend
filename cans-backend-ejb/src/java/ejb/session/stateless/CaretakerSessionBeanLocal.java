/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CaretakerEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CaretakerExistsException;
import util.exception.CaretakerNotFoundException;
import util.exception.ClientNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface CaretakerSessionBeanLocal {

    public Long createNewCaretaker(CaretakerEntity caretakerEntity, Long clientId) throws CaretakerExistsException, UnknownPersistenceException, InputDataValidationException, ClientNotFoundException;

    public boolean caretakerInDatabase(Long caretakerId);

    public CaretakerEntity retrieveCaretakerById(Long caretakerId) throws CaretakerNotFoundException;

    public List<CaretakerEntity> retrieveAllCaretakerEntities();

    public CaretakerEntity retrieveCaretakerByIdNumber(String idNumber) throws CaretakerNotFoundException;

    public CaretakerEntity retrieveCaretakerByUniqueId(Long caretakerId) throws CaretakerNotFoundException;

    public void deleteCaretaker(long caretakerId) throws CaretakerNotFoundException;

    public boolean updateActiveStatus(long caretakerId) throws CaretakerNotFoundException;
    
}
