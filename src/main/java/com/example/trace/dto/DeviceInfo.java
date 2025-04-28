package com.example.trace.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceInfo {
    private String deviceId;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
}
