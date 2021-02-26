package br.com.googleclient.config.security.openid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;

/**
 * Essa classe contem todas as claims que obtemos a partir do id_token retornado
 * após a autenticação do usuário no Identity Provider
 */
@Getter
@Setter
public class TokenIdClaims {

    @JsonProperty("azp")
    private String authorizedParty;

    @JsonProperty("aud")
    private String audience;

    @JsonProperty("sub")
    private String subjectIdentifier;

    @JsonProperty("email")
    private String email;

    @JsonProperty("at_hash")
    private String accessTokenHasValue;

    @JsonProperty("iss")
    private String issuerIdentifier;

    @JsonProperty("iat")
    private long issueAt;

    @JsonProperty("exp")
    private long expirationTime;

    /**
     * Cria uma instância de {@link TokenIdClaims}
     * @param jsonMapper
     * @param accessToken
     * @return
     */
    public static TokenIdClaims extractClaims(ObjectMapper jsonMapper, OAuth2AccessToken accessToken) {
        final String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
        final Jwt tokenDecoded = JwtHelper.decode(idToken);
        try {
            return jsonMapper.readValue(tokenDecoded.getClaims(), TokenIdClaims.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
