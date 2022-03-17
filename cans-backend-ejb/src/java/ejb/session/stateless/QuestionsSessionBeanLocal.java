/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.MainQuestionEntity;
import entity.QuestionEntity;
import entity.SubQuestionEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AgeGroupNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.MainQuestionExistsException;
import util.exception.QuestionNotFoundException;
import util.exception.SubModuleNotFoundException;
import util.exception.SubQuestionExistsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface QuestionsSessionBeanLocal {

    public QuestionEntity createMainQuestionForAgeGroup(Long ageId, MainQuestionEntity ques) throws AgeGroupNotFoundException, MainQuestionExistsException, UnknownPersistenceException, InputDataValidationException;

    public QuestionEntity createSubQuestionForSubModule(Long modId, SubQuestionEntity ques) throws SubModuleNotFoundException, SubQuestionExistsException, UnknownPersistenceException, InputDataValidationException;

    public QuestionEntity retrieveQuestionById(Long id) throws QuestionNotFoundException;

    public QuestionEntity retrieveQuestionByCode(String code) throws QuestionNotFoundException;

    public QuestionEntity retrieveQuestionByQuestionTitle(String questionTitle) throws QuestionNotFoundException;

    public List<QuestionEntity> retrieveAllQuestion();

}
