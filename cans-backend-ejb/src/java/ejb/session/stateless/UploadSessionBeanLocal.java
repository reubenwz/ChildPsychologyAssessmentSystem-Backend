/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.UploadEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.DataUploadException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface UploadSessionBeanLocal {

    public List<UploadEntity> retrieveAllUploads();

    public UploadEntity importRawData(String fileDirectory, String fileName, long adminUserId) throws UserNotFoundException, DataUploadException;

    public UploadEntity renderUnsuccesfulUpload(String upload_details, String fileName, long adminUserId) throws UserNotFoundException;
    
}
