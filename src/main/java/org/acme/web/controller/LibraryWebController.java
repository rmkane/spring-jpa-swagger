package org.acme.web.controller;

import org.acme.web.dto.response.AuthorResponse;
import org.acme.web.dto.response.BookResponse;
import org.acme.web.exception.ResourceNotFoundException;
import org.acme.web.service.AuthorService;
import org.acme.web.service.BookService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class LibraryWebController {

    private final AuthorService authorService;
    private final BookService bookService;

    public LibraryWebController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Library");
        return "home";
    }

    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("pageTitle", "Books");
        model.addAttribute("books", bookService.findAll());
        return "books/list";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable("id") @NonNull Long id, Model model) {
        BookResponse book = bookService.findById(id);
        model.addAttribute("pageTitle", book.getTitle());
        model.addAttribute("book", book);
        return "books/detail";
    }

    @GetMapping("/authors")
    public String authors(Model model) {
        model.addAttribute("pageTitle", "Authors");
        model.addAttribute("authors", authorService.findAll());
        return "authors/list";
    }

    @GetMapping("/authors/{id}")
    public String authorDetail(@PathVariable("id") @NonNull Long id, Model model) {
        AuthorResponse author = authorService.findById(id);
        model.addAttribute("pageTitle", author.getFirstName() + " " + author.getLastName());
        model.addAttribute("author", author);
        return "authors/detail";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("pageTitle", "Not found");
        mav.addObject("message", ex.getMessage());
        mav.setStatus(org.springframework.http.HttpStatus.NOT_FOUND);
        return mav;
    }
}
