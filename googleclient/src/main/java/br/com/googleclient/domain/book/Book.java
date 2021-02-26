package br.com.googleclient.domain.book;

import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "livro")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo")
    private String title;

    @Range(min = 0, max = 10)
    @Column(name = "nota")
    private int note;

    public Book(String title, int note) {
        super();
        this.title = title;
        this.note = note;
    }

    @SuppressWarnings("unused")
    public Book() {
    }
}
