package com.example.trace.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 허용 확장자 목록
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");

    public String saveFile(MultipartFile multipartFile, FileType fileType) throws IOException {
        // 1. 파일 유효성 검사
        validateFile(multipartFile);

        // 2. 안전한 파일명 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = generateSafeFileName(fileType, originalFilename);

        // 3. 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(detectContentType(originalFilename));

        // 4. S3 업로드 (퍼블릭 읽기 권한 추가)
        PutObjectRequest request = new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata);

        amazonS3.putObject(request);

        // 5. CloudFront URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void validateFile(MultipartFile file) {
        // 파일 확장자 검증
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {

            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "허용되지 않는 파일 형식"); // 상세한 예외처리는 나중에 구현
        }

        // 파일 크기 검증 (최대 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "파일 크기 초과"); // 상세한 예외처리는 나중에 구현
        }
    }

    private String generateSafeFileName(FileType fileType, String originalFilename) {
        // UUID + 원본파일명 조합
        String uuid = UUID.randomUUID().toString().substring(0, 12);
        String safeName = uuid + "_" + originalFilename.replace(" ", "_");

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

