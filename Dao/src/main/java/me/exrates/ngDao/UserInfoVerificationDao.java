package me.exrates.ngDao;

import me.exrates.model.ngModel.UserInfoVerificationDto;

public interface UserInfoVerificationDao {

    UserInfoVerificationDto save(UserInfoVerificationDto verificationDto);

    boolean delete(UserInfoVerificationDto verificationDto);

    UserInfoVerificationDto findByUserId(Integer userId);
}
