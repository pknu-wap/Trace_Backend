package com.example.trace.global.execption;

import com.example.trace.global.errorcode.FileErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FileException extends RuntimeException {
    private final FileErrorCode fileErrorCode;
}
