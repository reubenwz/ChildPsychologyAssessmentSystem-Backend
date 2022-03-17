/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DomainEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.DomainExistsException;
import util.exception.DomainNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface DomainSessionBeanLocal {

    public DomainEntity createNewDomain(DomainEntity domain) throws UnknownPersistenceException, DomainExistsException, InputDataValidationException;

    public DomainEntity retrieveDomainById(Long id) throws DomainNotFoundException;

    public List<DomainEntity> retrieveAllDomains();

    public List<DomainEntity> retrieveAllModules();

    public List<DomainEntity> retrieveAllNonModuleDomains();

    public List<DomainEntity> retrieveAllClientDomains();

    public List<DomainEntity> retrieveAllCaretakerDomains();

    public void updateDomains(List<DomainEntity> newDomains);
    
}
