package br.com.googleclient.config.security.openid;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableOAuth2Client
@RequiredArgsConstructor
public class GoogleOpenIdConnectConfig {

    private final DiscoveryDocument discovery;

    /**
     * configura o Identity Provider (Authorization Server) que a aplicação ira acessar
     *
     * @return AuthorizationCodeResourceDetails
     */
    @Bean
    public BaseOAuth2ProtectedResourceDetails protectedResourceDetails() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId(discovery.getClientId());
        details.setClientSecret(discovery.getClientSecret());
        details.setAccessTokenUri(discovery.getAccessTokenUri());
        details.setUserAuthorizationUri(discovery.getUserAuthorizationUri());
        details.setPreEstablishedRedirectUri(discovery.getRedirectUri());
        details.setScope(Arrays.asList("openid", "email", "profile"));
        details.setUseCurrentUri(false);
        return details;
    }

    /**
     * Configura o Bean OAth2ResTemplate que serve para obter os tokens de autenticação e de acesso.
     */
    @Bean
    public OAuth2RestTemplate googleOpenIdRestTemplate(OAuth2ClientContext clientContext) {
        OAuth2RestTemplate template = new OAuth2RestTemplate(
                protectedResourceDetails(), clientContext);
        template.setAccessTokenProvider(getAccessTokenProvider());
        return template;
    }

    private AccessTokenProviderChain getAccessTokenProvider() {
        return new AccessTokenProviderChain(
                Arrays.asList(new AuthorizationCodeAccessTokenProvider()));
    }


}
