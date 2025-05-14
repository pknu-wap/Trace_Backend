package com.example.trace.global.exception;

import com.example.trace.global.errorcode.FileErrorCode;
import lombok.Getter;


@Getter
public class FileException extends RuntimeException {
    private final FileErrorCode fileErrorCode;
    public FileException(FileErrorCode fileErrorCode) {
        super(fileErrorCode.getMessage());
        this.fileErrorCode = fileErrorCode;
    }
}
