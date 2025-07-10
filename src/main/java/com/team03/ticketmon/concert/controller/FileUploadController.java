package com.team03.ticketmon.concert.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team03.ticketmon._global.config.supabase.SupabaseProperties;
import com.team03.ticketmon._global.exception.StorageUploadException;
import com.team03.ticketmon._global.exception.SuccessResponse;
import com.team03.ticketmon._global.util.FileValidator;
import com.team03.ticketmon._global.util.UploadPathUtil;
import com.team03.ticketmon._global.util.uploader.StorageUploader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

	private final StorageUploader storageUploader;

	@PostMapping("/poster")
	public ResponseEntity<SuccessResponse<String>> uploadPoster(
			@RequestParam("file") MultipartFile file,
			@RequestParam(required = false) Long concertId
	) {
		try {
			// 파일 검증
			FileValidator.validate(file);

			// 업로드 경로 설정
			String path = concertId != null ?
					UploadPathUtil.getPosterPath(concertId, getFileExtension(file)) :
					"poster/temp/" + UUID.randomUUID();

			// ✅ 환경에 따라 자동으로 Supabase or S3 에 업로드
			String bucket = "poster"; // ✅ S3/Supabase 양쪽 모두에서 일관된 버킷 이름 사용
			String url = storageUploader.uploadFile(file, bucket, path);

			return ResponseEntity.ok(SuccessResponse.of("파일 업로드 성공", url));

		} catch (Exception e) {
			throw new StorageUploadException("포스터 업로드 실패", e);
		}
	}

	private String getFileExtension(MultipartFile file) {
		String filename = file.getOriginalFilename();
		return filename.substring(filename.lastIndexOf(".") + 1);
	}
}
