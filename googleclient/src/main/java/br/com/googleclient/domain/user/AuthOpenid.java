package br.com.googleclient.domain.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Entity
@Table(name = "autenticacao_openid")
public class AuthOpenid implements Serializable {

    @Id
    @Embedded
    private AuthIdentifier id;

    @Column(name = "authn_provider")
    private String provider;

    @Setter
    @Column(name = "authn_validade")
    private Date validate;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private User user;

    public AuthOpenid(User user, AuthIdentifier id, String provider, Date validate) {
        this.id = id;
        this.provider = provider;
        this.validate = validate;
        this.user = user;

        user.authenticate(this);
    }

    @SuppressWarnings("unused")
    public AuthOpenid() {
    }

    public boolean expired() {
        OffsetDateTime tokenValidateDate = OffsetDateTime.ofInstant(
                validate.toInstant(), ZoneId.systemDefault());

        OffsetDateTime now = OffsetDateTime.now(ZoneId.systemDefault());

        return now.isAfter(tokenValidateDate);
    }
}
