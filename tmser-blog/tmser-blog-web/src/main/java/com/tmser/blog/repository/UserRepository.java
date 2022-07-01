package com.tmser.blog.repository;

import com.tmser.blog.model.entity.User;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
    @Select("select * from users where username = #{username}")
    Optional<User> findByUsername(@NonNull String username);

    /**
     * Gets user by email.
     *
     * @param email email must not be blank
     * @return an optional user
     */
    @NonNull
    @Select("select * from users where email = #{email}")
    Optional<User> findByEmail(@NonNull String email);
}
