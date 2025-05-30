package com.example.trace.global.exception;

import com.example.trace.global.errorcode.MissionErrorCode;
import lombok.Getter;

@Getter
public class MissionException extends RuntimeException {
    private final MissionErrorCode missionErrorCode;
    public MissionException(MissionErrorCode missionErrorCode) {
        super(missionErrorCode.getMessage());
        this.missionErrorCode = missionErrorCode;
    }
}
