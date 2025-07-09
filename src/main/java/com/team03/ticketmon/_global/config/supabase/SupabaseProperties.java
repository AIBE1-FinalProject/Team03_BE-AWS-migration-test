package com.team03.ticketmon._global.config.supabase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

/**
 * Supabase 설정 값을 application.yml에서 바인딩해주는 구성 클래스입니다.
 *
 * <p>해당 클래스는 Spring Boot의 {@code @ConfigurationProperties}를 활용하여,
 * application.yml 또는 application-dev.yml 파일의 {@code supabase} 설정 항목을 자동으로 주입받습니다.</p>
 *
 * <p>📌 현재 서비스는 Supabase Auth를 사용하지 않으며, 모든 Storage 요청은 **{@code service_role} 키**를 기반으로 처리됩니다.</p>
 * <p>⚠️ **주의:** {@code service_role} 키는 강력한 권한을 가지므로, 백엔드 서버에서만 사용하고 **절대 클라이언트(브라우저)에 노출되어서는 안 됩니다.**</p>
 *
 * <ul>
 * <li>{@code supabase.url} : Supabase 프로젝트 기본 URL (API 엔드포인트)</li>
 * <li>{@code supabase.key} : Supabase API 키 (service_role 키 사용)</li>
 * <li>{@code supabase.profile-bucket} : 프로필 이미지 버킷 이름</li>
 * <li>{@code supabase.poster-bucket} : 포스터 이미지 버킷 이름</li>
 * <li>{@code supabase.docs-bucket} : 판매자 서류 버킷 이름</li>
 * </ul>
 *
 * <p>🎯 향후 AWS S3로 마이그레이션 시에도 이 구조를 유지하면서 설정값만 교체하면 되도록 설계되었습니다.</p>
 */
@Getter
@Setter
@Profile("supabase")
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProperties {

    /** Supabase 프로젝트의 URL (예: https://xxx.supabase.co) */
    private String url;

    /**
     * Supabase API 키 (service_role)
     *
     * <p>{@code service_role} 키를 사용하며, 백엔드에서만 접근하기 때문에 외부 노출 없이 안전하게 운용되어야 합니다.<br>
     * Supabase Auth 인증을 사용하지 않는 구조이므로, 스토리지 접근 시 이 키를 통해 RLS를 우회합니다.</p>
     */
    private String key;

    /** 프로필 이미지가 저장될 버킷 이름 */
    private String profileBucket;

    /** 포스터 이미지가 저장될 버킷 이름 */
    private String posterBucket;

    /** 판매자 권한 신청 시 제출하는 증빙 서류(사업자등록증 등)를 저장하는 버킷 이름 */
    private String docsBucket;
}
