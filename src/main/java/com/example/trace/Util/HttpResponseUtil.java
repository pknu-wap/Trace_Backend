package com.example.trace.Util;


import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 성공 응답 설정
    public static void setSuccessResponse(HttpServletResponse response, HttpStatus status, Object data) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", status.value());
        responseBody.put("message", "Success");
        responseBody.put("data", data);

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    // 실패 응답 설정
    public static void setErrorResponse(HttpServletResponse response, HttpStatus status, String error, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", status.value());
        responseBody.put("error", error);
        responseBody.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}