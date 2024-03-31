package com.devdive.studylog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    public TokenAuthenticationSuccessHandler(JwtProvider jwtProvider, TokenRepository tokenRepository) {
        this.jwtProvider = jwtProvider;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        tokenRepository.deleteByToken((String) authentication.getPrincipal());
        String accessToken = jwtProvider.createToken((String) authentication.getPrincipal());
        setAccessToken(response, accessToken);
    }

    private void setAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("accessToken", accessToken);
        String jsonString = objectMapper.writeValueAsString(jsonResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.getWriter().write(jsonString);
    }
}
