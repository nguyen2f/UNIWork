package com.uniwork.config;

import com.uniwork.interceptors.Payload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerPayloadConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

                String uri = request.getRequestURI();
                if (uri.startsWith("/swagger-ui")
                        || uri.startsWith("/v3/api-docs")
                        || uri.startsWith("/api-docs")) {

                    Payload payload = new Payload();
                    payload.setUserId(1L); // 👈 mock userId (tuỳ chỉnh tuỳ bạn)
                    request.setAttribute("payload", payload);
                }

                return true;
            }
        });
    }
}
