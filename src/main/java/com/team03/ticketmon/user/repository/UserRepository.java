package com.team03.ticketmon.user.repository;

import com.team03.ticketmon.user.domain.entity.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    @NotNull Optional<UserEntity> findById(@NotNull Long id);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findFirstByUsernameOrEmailOrNickname(String username, String email, String nickname);

    // 특정 Role을 가진 UserEntity 목록을 조회 (AdminSellerService에서 사용)
    List<UserEntity> findByRole(UserEntity.Role role);
}
