package com.product.hms.security.oauth2;

import com.product.hms.entity.UserEntity;
import com.product.hms.repository.UserRepository;
import com.product.hms.security.JwtTokenProvider;
import com.product.hms.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();

        UserEntity user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + principal.getEmail()));

        String token = tokenProvider.generateToken(user);

        // Lấy fullName từ customer
        String fullName = "";
        if (user.getCustomerEntity() != null) {
            fullName = user.getCustomerEntity().getFullName();
        } else if (user.getStaffEntity() != null) {
            fullName = user.getStaffEntity().getFullName();
        }

        // Encode các params để tránh lỗi URL
        try {
            return UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                    .queryParam("token", URLEncoder.encode(token, StandardCharsets.UTF_8.toString()))
                    .build()
                    .toUriString();
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding URL parameters", e);
            return frontendUrl + "/oauth2/redirect?error=encoding_error";
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}