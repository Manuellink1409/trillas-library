package com.hypermedia.library.book;

import com.hypermedia.library.common.PageResponse;
import com.hypermedia.library.exception.OperationNotPermittedException;
import com.hypermedia.library.history.BookTransactionHistory;
import com.hypermedia.library.history.BookTransactionHistoryRepository;
import com.hypermedia.library.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository historyRepository;

    public Integer saveBook(BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Book book = bookMapper.toBook(request);
        System.out.println(book.getId());
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("Not found book with id " + bookId));
    }


    public PageResponse<BookResponse> findAllBooks(int page,
                                                   int size,
                                                   Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest
                .of(page,size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                                                .map(bookMapper::toBookResponse)
                                                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page,
                                                          int size,
                                                          Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest
                .of(page,size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAllByOwner(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page,
                                                           int size,
                                                           Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest
                .of(page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> bookHistory = historyRepository
                .findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = bookHistory
                .stream()
                .map(bookMapper::toBorrowedResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                bookHistory.getNumber(),
                bookHistory.getSize(),
                bookHistory.getTotalElements(),
                bookHistory.getTotalPages(),
                bookHistory.isFirst(),
                bookHistory.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page,
                                                                   int size,
                                                                   Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest
                .of(page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> bookHistory = historyRepository
                .findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = bookHistory
                .stream()
                .map(bookMapper::toBorrowedResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                bookHistory.getNumber(),
                bookHistory.getSize(),
                bookHistory.getTotalElements(),
                bookHistory.getTotalPages(),
                bookHistory.isFirst(),
                bookHistory.isLast()
        );
    }

    public Integer updateShareableStatus(Integer id, Authentication connectedUser) {

        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found book with id " + id));
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot update books shareable status, because you're not the owner.");
        }
        book.setShareable(!book.isShareable());
        return bookRepository.save(book).getId();

    }

    public Integer updateArchivedStatus(Integer id,
                                     Authentication connectedUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found book with id " + id));
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot update books shareable status, because you're not the owner.");
        }
        book.setArchived(!book.isArchived());
        return bookRepository.save(book).getId();
    }

    public Integer borrowBook(Integer id,
                           Authentication connectedUser) {

        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found book with id " + id));
        User user = (User) connectedUser.getPrincipal();
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book cannot be borrowed");
        }
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowed = historyRepository.isAlreadyBorrowed(id, user.getId());

        if (isAlreadyBorrowed){
            throw new OperationNotPermittedException("The request book is already borrowed");
        }
        BookTransactionHistory history = BookTransactionHistory
                .builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return historyRepository.save(history).getId();

    }

    public Integer returnBorrowBook(Integer id,
                                    Authentication connectedUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found book with id " + id));
        User user = (User) connectedUser.getPrincipal();
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book cannot be borrowed");
        }
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }

        BookTransactionHistory bookTransactionHistory = historyRepository
                .findByBookAndUserId(id,user.getId())
                .orElseThrow(()-> new EntityNotFoundException("Not found book with id " + id));
        bookTransactionHistory.setReturned(true);
        return historyRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnApprovedBook(Integer id,
                                      Authentication connectedUser) {

        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found book with id " + id));
        User user = (User) connectedUser.getPrincipal();
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book cannot be borrowed");
        }
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }

        BookTransactionHistory bookTransactionHistory = historyRepository
                .findByBookAndOwnerId(id,user.getId())
                .orElseThrow(()-> new EntityNotFoundException("The book is not returned yet,"));
        bookTransactionHistory.setReturnApproved(true);
        return historyRepository.save(bookTransactionHistory).getId();

    }
}
