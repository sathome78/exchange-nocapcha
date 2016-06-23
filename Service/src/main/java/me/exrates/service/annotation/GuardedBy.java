package me.exrates.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Documented
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD})
public @interface GuardedBy {
    String value();
}
