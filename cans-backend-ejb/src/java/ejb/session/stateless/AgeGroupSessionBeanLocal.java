/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AgeGroupEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AgeGroupExistsException;
import util.exception.AgeGroupNotFoundException;
import util.exception.DomainNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface AgeGroupSessionBeanLocal {

    public AgeGroupEntity createNewAgeGroupForDomain(AgeGroupEntity age, Long id) throws AgeGroupExistsException, UnknownPersistenceException, InputDataValidationException, DomainNotFoundException;

    public AgeGroupEntity retrieveAgeGroupById(Long id) throws AgeGroupNotFoundException;

    public List<AgeGroupEntity> retrieveAllAgeGroups();

    public List<Long> retrieveAgeGroupIdbyAge(int age) throws AgeGroupNotFoundException;
    
}
