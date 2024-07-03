package com.hypermedia.library.book;

import com.hypermedia.library.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name="Book")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Integer> saveBook(@RequestBody @Valid BookRequest request,
                                         Authentication connectedUser) {
        return ResponseEntity.ok(bookService.saveBook(request,connectedUser));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId) {
        return ResponseEntity.ok(bookService.findBookById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(required = false, defaultValue = "0",name = "page") int page,
            @RequestParam(required = false, defaultValue = "10",name = "size") int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBooks(page,size,connectedUser));

    }

    @GetMapping("owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(required = false, defaultValue = "0",name = "page") int page,
            @RequestParam(required = false, defaultValue = "10",name = "size") int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page,size,connectedUser));
    }

    @GetMapping("borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(required = false, defaultValue = "0",name = "page") int page,
            @RequestParam(required = false, defaultValue = "10",name = "size") int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page,size,connectedUser));
    }

    @GetMapping("returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(required = false, defaultValue = "0",name = "page") int page,
            @RequestParam(required = false, defaultValue = "10",name = "size") int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page,size,connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(@PathVariable("book-id") Integer id,
                                                      Authentication connectedUser) {

        return ResponseEntity.ok(bookService.updateShareableStatus(id,connectedUser));

    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(@PathVariable("book-id") Integer id,
                                                      Authentication connectedUser) {

        return ResponseEntity.ok(bookService.updateArchivedStatus(id,connectedUser));

    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(@PathVariable("book-id") Integer id,
                                           Authentication connectedUser) {

        return ResponseEntity.ok(bookService.borrowBook(id,connectedUser));

    }

    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(@PathVariable("book-id") Integer id,
                                                        Authentication connectedUser) {

        return ResponseEntity.ok(bookService.returnBorrowBook(id,connectedUser));

    }

    @PatchMapping("/borrow/return/approved/{book-id}")
    public ResponseEntity<Integer> returnApprovedBook(@PathVariable("book-id") Integer id,
                                                    Authentication connectedUser) {

        return ResponseEntity.ok(bookService.returnApprovedBook(id,connectedUser));

    }


}
