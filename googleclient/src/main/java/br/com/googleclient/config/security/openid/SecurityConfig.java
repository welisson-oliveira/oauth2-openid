package br.com.googleclient.config.security.openid;

import br.com.googleclient.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2RestTemplate openidRestTemplate;
    private final ObjectMapper jsonMapper;
    private final UserRepository userRepository;
    private final OpenIdTokenServices tokenServices;
    private final OAuth2ProtectedResourceDetails resourceDetails;

    @Bean
    public OpenIdConnectFilter openIdConnectFilter() {
        OpenIdConnectFilter filter = new OpenIdConnectFilter(
                "/livros/**", "/google/callback");
        filter.setRestTemplate(openidRestTemplate);
        filter.setJsonMapper(jsonMapper);
        filter.setUserRepository(userRepository);
        filter.setTokenServices(tokenServices);
        filter.setResourceDetails(resourceDetails);
        return filter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String[] caminhosPermitidos = new String[]{
                "/", "/home", "/usuarios", "/google/login", "/google/callback",
                "/webjars/**", "/static/**", "/jquery*"
        };

        http
                .addFilterAfter(filterForClientOAuth2(), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(openIdConnectFilter(), OAuth2ClientContextFilter.class)
                .httpBasic()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/google/callback"))
                .and()
                .authorizeRequests()
                .antMatchers(caminhosPermitidos).permitAll()
                .anyRequest().authenticated()
                .and().logout()
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
                .csrf().disable();
    }

    /**
     * Inicia todo o fluxo de autorização do OAuth2, usando para isso o grant type que foi definido
     * na configuração do bean do tipo AuthorizationCodeResourceDetails dentro da classe {{@link GoogleOpenIdConnectConfig}.
     * @return
     */
    private Filter filterForClientOAuth2() {
        return new OAuth2ClientContextFilter();
    }
}
