package me.exrates.ngService.impl;


import me.exrates.model.User;
import me.exrates.model.ngModel.UserDocVerificationDto;
import me.exrates.model.ngModel.UserInfoVerificationDto;
import me.exrates.model.ngModel.enums.VerificationDocumentType;
import me.exrates.ngDao.UserDocVerificationDao;
import me.exrates.ngDao.UserInfoVerificationDao;
import me.exrates.ngService.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserVerificationServiceImpl implements UserVerificationService {

    private final UserInfoVerificationDao userVerificationDao;
    private final UserDocVerificationDao userDocVerificationDao;

    @Autowired
    public UserVerificationServiceImpl(UserInfoVerificationDao userVerificationDao,
                                       UserDocVerificationDao userDocVerificationDao) {
        this.userVerificationDao = userVerificationDao;
        this.userDocVerificationDao = userDocVerificationDao;
    }

    @Override
    public UserInfoVerificationDto save(UserInfoVerificationDto verificationDto) {
        return userVerificationDao.save(verificationDto);
    }

    @Override
    public UserDocVerificationDto save(UserDocVerificationDto verificationDto) {
        return userDocVerificationDao.save(verificationDto);
    }

    @Override
    public boolean delete(UserInfoVerificationDto verificationDto) {
        return userVerificationDao.delete(verificationDto);
    }

    @Override
    public boolean delete(UserDocVerificationDto verificationDto) {
        return userDocVerificationDao.delete(verificationDto);
    }

    @Override
    public UserInfoVerificationDto findByUser(User user) {
        return userVerificationDao.findByUserId(user.getId());
    }

    @Override
    public UserDocVerificationDto findByUserAndDocumentType(User user, VerificationDocumentType type) {
        return userDocVerificationDao.findByUserIdAndDocumentType(user.getId(), type);
    }

    @Override
    public List<UserDocVerificationDto> findDocsByUser(User user) {
        return userDocVerificationDao.findAllByUser(user);
    }

}
