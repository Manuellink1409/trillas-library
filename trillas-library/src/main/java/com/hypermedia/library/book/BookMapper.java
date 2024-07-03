package com.hypermedia.library.book;

import com.hypermedia.library.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .isbn(request.isbn())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .title(request.title())
                .archived(false)
                .shareable(request.shareable())
                .bookCover("")
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .owner(book.getOwner().getName())
                //.cover(book.getBookCover())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isArchived())
                .build();
    }

    public BorrowedBookResponse toBorrowedResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
