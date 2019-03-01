package me.exrates.ngService;

import me.exrates.model.User;
import me.exrates.model.ngModel.UserDocVerificationDto;
import me.exrates.model.ngModel.UserInfoVerificationDto;
import me.exrates.model.ngModel.enums.VerificationDocumentType;

import java.util.List;

public interface UserVerificationService {

    UserInfoVerificationDto save(UserInfoVerificationDto verificationDto);

    UserDocVerificationDto save(UserDocVerificationDto verificationDto);

    boolean delete(UserInfoVerificationDto verificationDto);

    boolean delete(UserDocVerificationDto verificationDto);

    UserInfoVerificationDto findByUser(User user);

    UserDocVerificationDto findByUserAndDocumentType(User user, VerificationDocumentType type);

    List<UserDocVerificationDto> findDocsByUser(User user);
}
