package com.nl.Nutso.web;

import com.nl.Nutso.model.dto.AddBookDTO;
import com.nl.Nutso.model.dto.BookDetailDTO;
import com.nl.Nutso.model.dto.BookSummaryDTO;
import com.nl.Nutso.model.enums.CategoryEnum;
import com.nl.Nutso.service.BookService;
import com.nl.Nutso.service.exception.ObjectNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("categories")
    public CategoryEnum[] categories() {
        return CategoryEnum.values();
    }

    @GetMapping("/add")
    public String add(Model model) {

        //TODO check if that's needed
        if (!model.containsAttribute("addBookDTO")) {
            model.addAttribute("addBookDTO", AddBookDTO.empty());
        }

        return "book-add";
    }

    @PostMapping("/add")
    public String add(
            @Valid AddBookDTO addBookDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("addBookDTO", addBookDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addBookDTO", bindingResult);
            return "redirect:/books/add";
        }


        UUID newBookUUID = bookService.addBook(addBookDTO);

        return "redirect:/books/" + newBookUUID;
    }

    @GetMapping("/{uuid}")
    public String details(@PathVariable("uuid") UUID uuid, Model model) {

        BookDetailDTO bookDetailDTO = bookService
                .getBookDetail(uuid)
                .orElseThrow(() -> new ObjectNotFoundException("Book with uuid " + uuid + " not found!"));

        model.addAttribute("book", bookDetailDTO);

        return "book-details";
    }

    @GetMapping("/all")
    public String all(Model model,
                      @PageableDefault(
                              size = 6,
                              sort = "title"
                      ) Pageable pageable) {

        Page<BookSummaryDTO> allBooks = bookService.getAllBooks(pageable);
        model.addAttribute("books", allBooks);

        return "books";
    }


}
