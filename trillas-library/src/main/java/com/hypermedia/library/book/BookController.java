package com.hypermedia.library.book;

import com.hypermedia.library.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Long> saveBook(@RequestBody BookRequest request,
                                         Authentication connectedUser) {
        return ResponseEntity.ok(bookService.saveBook(request,connectedUser));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Long bookId) {
        return ResponseEntity.ok(bookService.findBookById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(required = false, defaultValue = "0",name = "page") int page,
            @RequestParam(required = false, defaultValue = "10",name = "size") int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBooks(page,size,connectedUser));

    }


}
