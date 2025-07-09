package com.team03.ticketmon.user.service;

import com.team03.ticketmon.user.domain.entity.UserEntity;
import com.team03.ticketmon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private final UserRepository userRepository;

    @Override
    public Optional<UserEntity> findUserEntityByUserId(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<UserEntity> findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
}
