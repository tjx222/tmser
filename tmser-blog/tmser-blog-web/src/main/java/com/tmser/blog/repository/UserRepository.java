package com.tmser.blog.repository;

import com.tmser.blog.model.entity.User;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * User repository.
 *
 * @author johnniang
 */
@Mapper
public interface UserRepository extends BaseRepository<User> {

    /**
     * Gets user by username.
     *
     * @param username username must not be blank
     * @return an optional user
     */
    @NonNull
    Optional<User> findByUsername(@NonNull String username);

    /**
     * Gets user by email.
     *
     * @param email email must not be blank
     * @return an optional user
     */
    @NonNull
    Optional<User> findByEmail(@NonNull String email);
}
