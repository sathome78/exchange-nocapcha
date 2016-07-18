package me.exrates.controller.advice;

import me.exrates.model.UserFile;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private final MessageSource messageSource;
    private final UserService userService;

    @Autowired
    public GlobalControllerExceptionHandler(final MessageSource messageSource, final UserService userService) {
        this.messageSource = messageSource;
        this.userService = userService;
    }

    @ExceptionHandler(MultipartException.class)
    public ModelAndView handleMultipartConflict(final Locale locale) {
        final User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final me.exrates.model.User user = userService.findByEmail(principal.getUsername());
        final ModelAndView mav = new ModelAndView("settings");
        final List<UserFile> userFiles = userService.findUserDoc(user.getId());
        mav.addObject("user", user);
        mav.addObject("errorNoty", messageSource.getMessage("admin.errorUploadFiles", null, locale));
        mav.addObject("userFiles", userFiles);
        return mav;
    }
}
