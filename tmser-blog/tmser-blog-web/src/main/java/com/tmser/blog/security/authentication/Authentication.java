package com.tmser.blog.security.authentication;

import org.springframework.lang.NonNull;
import com.tmser.blog.security.support.UserDetail;

/**
 * Authentication.
 *
 * @author johnniang
 */
public interface Authentication {

    /**
     * Get user detail.
     *
     * @return user detail
     */
    @NonNull
    UserDetail getDetail();
}
