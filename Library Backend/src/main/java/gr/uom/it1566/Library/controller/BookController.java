package gr.uom.it1566.Library.controller;

import gr.uom.it1566.Library.dto.SearchBookDto;
import gr.uom.it1566.Library.entity.BookEntity;
import gr.uom.it1566.Library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;



@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    @GetMapping("/search")
    public Flux<BookEntity> searchBooks(@RequestBody SearchBookDto dto) {
        return bookService.searchBooks(dto.getQuery());
    }

}
