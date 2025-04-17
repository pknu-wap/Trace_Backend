package com.example.trace.file;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileTempController {

    private final S3UploadService S3UploadService;

    @PostMapping("/post")
    public ResponseEntity<String > uploadFile(@RequestParam("file") MultipartFile multipartFile){
        try {
            String fileUrl = S3UploadService.saveFile(multipartFile, FileType.POST);
            System.out.println("File name: " + multipartFile.getOriginalFilename());
            System.out.println("File size: " + multipartFile.getSize());
            return ResponseEntity.ok("File uploaded successfully. URL: " + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

}
