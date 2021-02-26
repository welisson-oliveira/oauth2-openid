package br.com.googleclient.domain.user.authentication;

import br.com.googleclient.domain.user.AuthOpenid;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Arrays;
import java.util.Collection;

@Getter
public class AuthenticatedUser implements UserDetails {

    private OAuth2AccessToken token;

    private AuthOpenid authOpenid;

    public AuthenticatedUser(final AuthOpenid authOpenid, final OAuth2AccessToken token) {
        this.token = token;
        this.authOpenid = authOpenid;
    }

    public AuthenticatedUser() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return token.getValue();
    }

    @Override
    public String getUsername() {
        return authOpenid.getUser().getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !token.isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !authOpenid.expired();
    }

    @Override
    public boolean isEnabled() {
        return !token.isExpired();
    }
}
