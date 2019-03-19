package me.exrates.ngDao;

import me.exrates.model.User;
import me.exrates.model.ngModel.UserDocVerificationDto;
import me.exrates.model.ngModel.enums.VerificationDocumentType;

import java.util.List;

public interface UserDocVerificationDao {

    UserDocVerificationDto save(UserDocVerificationDto verificationDto);

    boolean delete(UserDocVerificationDto verificationDto);

    UserDocVerificationDto findByUserIdAndDocumentType(Integer userId, VerificationDocumentType documentType);

    List<UserDocVerificationDto> findAllByUser(User user);

}
