package com.team03.ticketmon.seller_application.repository;

import com.team03.ticketmon.seller_application.domain.SellerApplication;
import com.team03.ticketmon.seller_application.domain.SellerApplication.SellerApplicationStatus;
import com.team03.ticketmon.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface SellerApplicationRepository extends JpaRepository<SellerApplication, Long> {

    // 특정 userId로 신청 정보를 조회 (유저 당 여러 개의 신청서를 가질 수 있으므로 List 반환)
//    List<SellerApplication> findByUserId(Long userId);
    List<SellerApplication> findByUser(UserEntity user); // UserEntity 객체로 조회하도록 변경

    // 특정 userId의 특정 상태 신청이 존재하는지 확인 (예: SUBMITTED 상태의 신청 여부)
//    boolean existsByUserIdAndStatus(Long userId, SellerApplicationStatus status);
    boolean existsByUserAndStatus(UserEntity user, SellerApplicationStatus status); // UserEntity 객체로 조회하도록 변경

    // 특정 상태의 신청 목록 조회 (관리자 페이지용, 예: SUBMITTED 상태 목록)
    List<SellerApplication> findByStatus(SellerApplicationStatus status);

    // 가장 최신 (현재 진행 중인 또는 마지막으로 처리된) 신청서 조회
    // activeStatuses 예시: List.of(SellerApplicationStatus.SUBMITTED, SellerApplicationStatus.ACCEPTED)
//    Optional<SellerApplication> findTopByUserIdAndStatusInOrderByCreatedAtDesc(Long userId, List<SellerApplicationStatus> statuses);
    Optional<SellerApplication> findTopByUserAndStatusInOrderByCreatedAtDesc(UserEntity user, List<SellerApplicationStatus> statuses); // UserEntity 객체로 조회하도록 변경

    // 특정 UserEntity에 대한 가장 최신 SellerApplication을 조회 (revokeSellerRole에서 사용)
    Optional<SellerApplication> findTopByUserOrderByCreatedAtDesc(UserEntity user);

    /* 스케줄러를 위한 추가 조회 메서드 예시 (개인정보 보호 정책 관련) */
    // WITHDRAWN 상태이고, 개인정보 마스킹 시점(maskedAt)이 null이며, createdAt이 특정 시간 이전인 목록 조회
    List<SellerApplication> findByStatusAndMaskedAtIsNullAndCreatedAtBefore(SellerApplicationStatus status, LocalDateTime dateTime);

    // REJECTED 상태이고, 실제 파일 삭제 시점(deletedAt)이 null이며, updatedAt이 특정 시간 이전인 목록 조회
    List<SellerApplication> findByStatusAndDeletedAtIsNullAndUpdatedAtBefore(SellerApplicationStatus status, LocalDateTime dateTime);

    // REVOKED 상태이고, 개인정보 마스킹 시점(maskedAt)이 null이며, updatedAt이 특정 시간 이전인 목록 조회
    List<SellerApplication> findByStatusAndMaskedAtIsNullAndUpdatedAtBefore(SellerApplicationStatus status, LocalDateTime dateTime);

    // 추가: 특정 사업자등록번호가 특정 상태 목록에 존재하는지 확인하는 메서드
    boolean existsByBusinessNumberAndStatusIn(String businessNumber, List<SellerApplicationStatus> statuses);

    /**
     * 특정 ID의 판매자 신청서를 UserEntity와 함께 Eager Loading하여 조회합니다.
     * N+1 문제를 방지하고, 신청서와 사용자 정보를 한 번에 가져올 때 유용합니다.
     *
     * @param id 조회할 SellerApplication의 ID
     * @return 조회된 SellerApplication (UserEntity가 로딩된 상태)
     */
    @EntityGraph(attributePaths = "user") // 'user' 필드를 Eager Loading (Fetch Join)
    @Query("SELECT sa FROM SellerApplication sa WHERE sa.id = :id")
    Optional<SellerApplication> findByIdWithUser(@Param("id") Long id);
}