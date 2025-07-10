package com.team03.ticketmon.user.service;

import com.team03.ticketmon._global.exception.BusinessException;
import com.team03.ticketmon._global.exception.ErrorCode;
import com.team03.ticketmon._global.util.FileValidator;
import com.team03.ticketmon._global.util.UploadPathUtil;
import com.team03.ticketmon._global.util.uploader.StorageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 사용자 프로필 이미지 관리 서비스 클래스입니다.
 *
 * 환경에 따라 Supabase 또는 S3를 유연하게 사용할 수 있도록 리팩토링되었습니다.
 * 기존 코드 구조를 최대한 유지하면서 스토리지 의존성만 추상화했습니다.
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final StorageUploader storageUploader; // 환경별 구현체가 자동 주입됨 (Supabase 또는 S3)

    // 환경별 버킷 설정 (application.yml에서 주입)
    @Value("${supabase.profile-bucket:#{null}}")
    private String supabaseProfileBucket;

    @Value("${cloud.aws.s3.bucket:#{null}}")
    private String s3Bucket;

    /**
     * 프로필 이미지를 업로드하고 URL을 반환합니다.
     *
     * @param profileImage 업로드할 프로필 이미지 파일
     * @return 업로드된 파일의 공개 URL, 파일이 없거나 비어있으면 빈 문자열 반환
     * @throws IllegalArgumentException 파일의 Content-Type을 확인할 수 없는 경우
     * @throws BusinessException 스토리지 설정이 올바르지 않은 경우
     */
    @Override
    public String uploadProfileAndReturnUrl(MultipartFile profileImage) {
        if (profileImage == null || profileImage.isEmpty()) {
            return "";
        }

        // 파일 유효성 검사 (FileValidator 사용)
        FileValidator.validate(profileImage);

        String fileUUID = UUID.randomUUID().toString();
        String contentType = profileImage.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("파일의 Content-Type을 확인할 수 없습니다.");
        }

        String fileExtension = UploadPathUtil.getExtensionFromMimeType(contentType);
        String filePath = UploadPathUtil.getProfilePath(fileUUID, fileExtension);

        // 환경에 따른 버킷 결정
        String bucketName = getBucketNameForProfile();

        return storageUploader.uploadFile(profileImage, bucketName, filePath);
    }

    /**
     * 프로필 이미지를 삭제합니다.
     *
     * @param profileImageUrl 삭제할 프로필 이미지의 URL
     * @throws BusinessException 스토리지 설정이 올바르지 않은 경우
     */
    @Override
    public void deleteProfileImage(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isEmpty()) {
            return;
        }

        // 환경에 따른 버킷 결정
        String bucketName = getBucketNameForProfile();

        storageUploader.deleteFile(bucketName, profileImageUrl);
    }

    /**
     * 환경에 따른 프로필 이미지 저장용 버킷명 결정
     * Supabase 환경에서는 supabase.profile-bucket 값을,
     * S3 환경에서는 cloud.aws.s3.bucket 값을 사용합니다.
     *
     * @return 환경에 맞는 버킷명
     * @throws BusinessException 스토리지 설정이 올바르지 않은 경우
     */
    private String getBucketNameForProfile() {
        if (supabaseProfileBucket != null && !supabaseProfileBucket.isEmpty()) {
            // Supabase 환경
            return supabaseProfileBucket;
        } else if (s3Bucket != null && !s3Bucket.isEmpty()) {
            // S3 환경
            return s3Bucket;
        } else {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "스토리지 설정이 올바르지 않습니다.");
        }
    }
}