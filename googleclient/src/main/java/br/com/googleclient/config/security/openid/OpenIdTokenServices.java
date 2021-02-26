package br.com.googleclient.config.security.openid;

import br.com.googleclient.domain.user.AuthIdentifier;
import br.com.googleclient.domain.user.AuthOpenid;
import br.com.googleclient.domain.user.User;
import br.com.googleclient.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Essa classe tem como objetivo salvar os dados de identificação do usuário
 * assim que o mesmo for autenticado pelo Identity Provider
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OpenIdTokenServices {

    private final UserRepository userRepository;
    private final ObjectMapper jsonMapper;
    private final UserInfoService userInfoService;

    private Date getDateTime(long timestamp){
        return new Date(timestamp * 1000);
    }

    /**
     * Obtem as claims do usuario, cria um novo na nossa base de dados se necessario e atualiza os dados dos usuarios ja existentes.
     * @param accessToken
     */
    public void saveAccessToken(OAuth2AccessToken accessToken){
        // obtem as claims do usuario e verifica na base de dados se o usuario ja usa uma conta Google
        // para fazer login em nossa aplicação.
        TokenIdClaims tokenIdClaims = TokenIdClaims.extractClaims(jsonMapper, accessToken);
        Optional<User> authenticatedUser = userRepository.findAuthenticatedUser(new AuthIdentifier(tokenIdClaims.getSubjectIdentifier()).getValue());
        // fim

        // se não existir um usuario, criamos um novo
        User user = authenticatedUser.orElseGet(() -> {
            User newUser = new User(tokenIdClaims.getEmail(), tokenIdClaims.getEmail());

            new AuthOpenid(newUser, new AuthIdentifier(tokenIdClaims.getSubjectIdentifier()), tokenIdClaims.getIssuerIdentifier(), getDateTime(tokenIdClaims.getExpirationTime()));
            return newUser;
        });
        // fim

        // caso o usuario ja exista atualizamos a data de validade da autenticação.
        if(user.getAuthOpenid().expired()){
            final AuthOpenid authOpenid = user.getAuthOpenid();
            authOpenid.setValidate(getDateTime(tokenIdClaims.getExpirationTime()));
        }
        // fim

        // atualiza os dados do usuario em nossa base de dados
        final Map<String, String> userInfo = userInfoService.getUserInfoFor(accessToken);
        final String username = userInfo.get("name");
        user.changeName(username);
        userRepository.save(user);

    }
}
