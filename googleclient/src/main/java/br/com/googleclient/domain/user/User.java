package br.com.googleclient.domain.user;

import br.com.googleclient.domain.book.Bookcase;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "usuario")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String name;

    private String email;

    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "usuario_id")
    private AuthOpenid authOpenid;

    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "usuario_id")
    private Bookcase bookcase = new Bookcase();

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.bookcase.setUser(this);
    }

    @SuppressWarnings("unused")
    public User(){
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void authenticate(AuthOpenid authOpenid) {
        this.authOpenid = authOpenid;
    }
}
