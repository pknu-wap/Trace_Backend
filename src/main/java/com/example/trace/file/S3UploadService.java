package com.example.trace.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.trace.global.errorcode.FileErrorCode;
import com.example.trace.global.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 허용 확장자 목록
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif","webp");

    public String saveFile(MultipartFile multipartFile, FileType fileType,String providerId) throws IOException {

        // 1. 파일 유효성 검사
        validateFile(multipartFile);
        log.info("파일 유효성 확인 완료");

        // 2. 안전한 파일명 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = generateSafeFileName(fileType, originalFilename,providerId);
        log.info("파일명 생성 완료: {}", fileName);

        // 3. 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(detectContentType(originalFilename));
        log.info("메타데이터 설정 완료: {}", metadata);

        // 4. S3 업로드 (퍼블릭 읽기 권한 추가)
        PutObjectRequest request = new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata);
        amazonS3.putObject(request);

        log.info("파일 S3 업로드 완료: {}", fileName);

        // 5. CloudFront URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void validateFile(MultipartFile file) {
        // 파일 확장자 검증
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {

            throw new FileException(FileErrorCode.UNSUPPORTED_MEDIA_FORMAT);
        }

        // 파일 크기 검증 (최대 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileException(FileErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    private String generateSafeFileName(FileType fileType, String originalFilename,String providerId) {
        // UUID + 원본파일명 조합
        String uuid = UUID.randomUUID().toString().substring(0, 12);
        String safeName = uuid + "_" + originalFilename.replace(" ", "_") + "_" + providerId;

        // 타입별 디렉토리 분리
        return fileType.getPath() + safeName;
    }

    private String detectContentType(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }
}

