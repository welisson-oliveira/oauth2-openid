package br.com.googleclient.config.security.openid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * busca os dados do usuario.
 */
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final DiscoveryDocument discoveryDocument;

    /**
     * busca os dados do usuario atraves do endpoint userInfo fornecido no momento de configuração da aplicação no Authorization Server
     * @param accessToken
     * @return
     */
    public Map<String, String> getUserInfoFor(OAuth2AccessToken accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(getHeader(accessToken.getValue()), HttpMethod.GET, URI.create(discoveryDocument.getUserInfoEndpoint()));

        ResponseEntity<Map> result = restTemplate.exchange(requestEntity, Map.class);

        if(result.getStatusCode().is2xxSuccessful()){
            return result.getBody();
        }

        throw new RuntimeException("Não foi possível obter os dados do usuário");

    }

    private MultiValueMap getHeader(final String accessToken){
        MultiValueMap<String, String> httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        return httpHeaders;
    }

}
