package com.example.trace.global.execption;

import com.example.trace.global.errorcode.PostErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PostExecption extends RuntimeException {
    private final PostErrorCode postErrorCode;
}

