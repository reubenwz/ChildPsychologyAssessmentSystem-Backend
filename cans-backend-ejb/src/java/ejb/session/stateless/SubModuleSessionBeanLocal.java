/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SubModuleEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.QuestionNotFoundException;
import util.exception.QuestionTypeInaccurateException;
import util.exception.SubModuleExistsException;
import util.exception.SubModuleNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface SubModuleSessionBeanLocal {

    public SubModuleEntity retreiveModuleById(Long id) throws SubModuleNotFoundException;

    public SubModuleEntity createNewSubModuleWithManyMainQues(SubModuleEntity mod, List<Long> mainQuesIds) throws SubModuleExistsException, UnknownPersistenceException, InputDataValidationException, QuestionTypeInaccurateException, QuestionNotFoundException;

    public SubModuleEntity createNewSubModule(SubModuleEntity mod, Long mainQuesId) throws SubModuleExistsException, UnknownPersistenceException, InputDataValidationException, QuestionTypeInaccurateException, QuestionNotFoundException;

    public List<SubModuleEntity> retrieveSubModuleByCategory(Boolean isInfo);

}
