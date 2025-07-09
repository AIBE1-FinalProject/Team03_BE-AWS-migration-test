package com.team03.ticketmon.user.service;

import com.team03.ticketmon.user.domain.entity.UserEntity;

import java.util.Optional;

public interface UserEntityService {
    Optional<UserEntity> findUserEntityByUserId(Long userId);
    Optional<UserEntity> findUserEntityByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsById(Long userId);
    UserEntity save(UserEntity user);
}
