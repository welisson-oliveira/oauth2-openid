package br.com.googleclient.domain.book;

import br.com.googleclient.domain.user.User;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
@Entity
@Table(name = "estante")
@Getter
public class Bookcase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "usuario_id")
    private User user;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "estante_id")
    private List<Book> books = new ArrayList<>();

    public boolean hasBooks() {
        return books.size() > 0;
    }

    public Collection<Book> allBooks() {
        return Collections.unmodifiableCollection(books);
    }

    public void add(Book book) {
        books.add(book);
    }

    public void setUser(User user) {
        this.user = user;
    }
}
