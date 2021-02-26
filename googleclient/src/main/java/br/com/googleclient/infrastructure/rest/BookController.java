package br.com.googleclient.infrastructure.rest;

import br.com.googleclient.domain.book.Book;
import br.com.googleclient.domain.book.BookData;
import br.com.googleclient.domain.user.User;
import br.com.googleclient.domain.user.UserRepository;
import br.com.googleclient.domain.user.authentication.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/livros")
@RequiredArgsConstructor
public class BookController {
    private final UserRepository userRepository;

    @RequestMapping(value = "/principal", method = RequestMethod.GET)
    public ModelAndView principal() {
        ModelAndView mv = new ModelAndView("livros/principal");

        mv.addObject("dadosDoLivro", new BookData());
        mv.addObject("livros", bookOwners().getBookcase().allBooks());

        return mv;
    }

    @RequestMapping(value = "/principal", method = RequestMethod.POST)
    public ModelAndView addBook(@Valid BookData dadosDoLivro, BindingResult bindingResult) {
        ModelAndView mv = new ModelAndView("livros/principal");

        User user = bookOwners();

        if (bindingResult.hasErrors()) {
            mv.addObject("livros", user.getBookcase().allBooks());
            mv.addObject("dadosDoLivro", dadosDoLivro);
            return mv;
        }

        Book newBook = new Book(dadosDoLivro.getTitle(), dadosDoLivro.getNote());
        user.getBookcase().add(newBook);

        userRepository.save(user);

        mv.addObject("livros", user.getBookcase().allBooks());
        mv.addObject("dadosDoLivro", new BookData());

        return mv;
    }

    private User bookOwners() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser bookOwner = (AuthenticatedUser) authentication.getPrincipal();

        Optional<User> user = userRepository.findAuthenticatedUser(
                bookOwner.getAuthOpenid().getId().getValue());

        return user.orElseThrow(
                () -> new RuntimeException("É preciso ter um usuário logado para acessar os livros"));
    }
}
