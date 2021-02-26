package br.com.googleclient.config.security.openid;

import br.com.googleclient.domain.user.AuthIdentifier;
import br.com.googleclient.domain.user.User;
import br.com.googleclient.domain.user.UserRepository;
import br.com.googleclient.domain.user.authentication.AuthenticatedUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro responsavel por interceptar o acesso as areas restritas do sistema.
 * Solicita a autenticação do usuario caso não esteja logado.
 */
@Setter
public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {

    private ApplicationEventPublisher eventPublisher;
    private OAuth2RestTemplate restTemplate;
    private ObjectMapper jsonMapper;
    private UserRepository userRepository;
    private OpenIdTokenServices tokenServices;
    private OAuth2ProtectedResourceDetails resourceDetails;
    private final RequestMatcher matcherLocal;

    public OpenIdConnectFilter(String defaultFilterProcessesUrl, String callback) {
        super(new OrRequestMatcher(
                new AntPathRequestMatcher(defaultFilterProcessesUrl),
                new AntPathRequestMatcher(callback)));
        this.matcherLocal = new AntPathRequestMatcher(defaultFilterProcessesUrl);
        setAuthenticationManager(new NoopAuthenticationManager());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        super.setApplicationEventPublisher(eventPublisher);
    }

    /**
     * necessario para que a autenticação funcione corretamente
     *
     * @param req
     * @param res
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (matcherLocal.matches(request)) {
            restTemplate.getAccessToken();
            chain.doFilter(req, res);
        } else {
            super.doFilter(req, res, chain);
        }
    }

    /**
     * tenta obter um token de autenticação OpenId
     * em caso de sucesso, salva os dados do usuario no banco
     * caso contrario, o usuario será redirecionado para a tela de autenticação do Authorization Server
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        final OAuth2AccessToken accessToken;
        try {
            accessToken = restTemplate.getAccessToken();
            tokenServices.saveAccessToken(accessToken);
        } catch (OAuth2Exception e) {
            BadCredentialsException error = new BadCredentialsException("Não foi possível obter o token", e);
            publish(new OAuth2AuthenticationFailureEvent(error));
            throw error;
        }

        // é atraves de um objeto Authentication que o Spring Security vai reconhecer o usuário como  autenticado.
        try {
            final TokenIdClaims tokenIdClaims = TokenIdClaims.extractClaims(jsonMapper, accessToken);
            final User user = userRepository.findAuthenticatedUser(new AuthIdentifier(tokenIdClaims.getSubjectIdentifier()).getValue()).get();
            final AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getAuthOpenid(), accessToken);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
            publish(new AuthenticationSuccessEvent(authentication));
            return authentication;
        } catch (InvalidTokenException e) {
            BadCredentialsException error = new BadCredentialsException("Não foi possivel obter os detalhes do token", e);
            publish(new OAuth2AuthenticationFailureEvent(error));
            throw error;
        }

    }

    private void publish(ApplicationEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }
    }

    private static class NoopAuthenticationManager implements AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }
    }
}
