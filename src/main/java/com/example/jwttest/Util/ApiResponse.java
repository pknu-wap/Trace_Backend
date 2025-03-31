package com.example.jwttest.Util;

public class ApiResponse<T> {
    private final boolean isSuccess;
    private final String code;
    private final String message;
    private final T data;

    // Success response constructor
    private ApiResponse(T data) {
        this.isSuccess = true;
        this.code = "200";
        this.message = "Success";
        this.data = data;
    }

    // Error response constructor (예시용, 현재 코드에서는 사용되지 않음)
    private ApiResponse(String code, String message) {
        this.isSuccess = false;
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(data);
    }

    // 필요시 에러 응답 생성을 위한 메서드 추가 가능
    public static <T> ApiResponse<T> onFailure(String code, String message) {
        return new ApiResponse<>(code, message);
    }

    // Getter methods
    public boolean isSuccess() { return isSuccess; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
