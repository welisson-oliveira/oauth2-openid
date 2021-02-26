package br.com.googleclient.domain.user;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Embeddable
public class AuthIdentifier implements Serializable {
    @Column(name = "authn_id")
    private String value;

    @SuppressWarnings("unused")
    public AuthIdentifier() {
    }

    public AuthIdentifier(String value) {
        this.value = value;
    }
}
