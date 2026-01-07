package com.uniwork.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniwork.model.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");

        ApiResponse<?> apiResponse =
                ApiResponse.error(errorCode.getCode(), errorCode.getMessage());

        response.getWriter().write(
                objectMapper.writeValueAsString(apiResponse)
        );
    }
}
