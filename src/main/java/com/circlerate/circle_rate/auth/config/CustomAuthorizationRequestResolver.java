package com.circlerate.circle_rate.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;


public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo, String baseUri) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, baseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        return customizeAuthorizationRequest(req, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String id) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, id);
        return customizeAuthorizationRequest(req, request);
    }

    //auth url - /oauth2/authorization/google
    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest req, HttpServletRequest request) {
        if (req == null) return null;
        String userRole = request.getParameter("role"); // Or get from cookie/session
        if (userRole != null) {
            String encodedState = Base64.getEncoder().encodeToString(userRole.getBytes());
            return OAuth2AuthorizationRequest.from(req).state(encodedState).build();
        }
        return req;
    }

}
