package br.com.googleclient.config.security.openid;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * essa classe tem como objetivo mapear as propriedades do arquivo application.yml
 */
@Component
@Getter
public class DiscoveryDocument {
    @Value("${google.client_id}")
    private String clientId;
    @Value("${google.client_secret}")
    private String clientSecret;
    @Value("${google.access_token_uri}")
    private String accessTokenUri;
    @Value("${google.user_authorization_uri}")
    private	String	userAuthorizationUri;
    @Value("${google.redirect_uri}")
    private String redirectUri;
    @Value("${google.userinfo_endpoint}")
    private String userInfoEndpoint;
}
