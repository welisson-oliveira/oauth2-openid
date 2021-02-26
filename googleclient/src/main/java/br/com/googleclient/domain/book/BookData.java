package br.com.googleclient.domain.book;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

@ToString
@Getter
@Setter
public class BookData {

    @NotEmpty
    private String title;
    @Range(min = 0, max = 10)
    private int note;
}
