package com.jencys.books.service.impl;

import com.jencys.books.dto.BookDTO;
import com.jencys.books.exception.BookNotFoundException;
import com.jencys.books.exception.DuplicateISBNException;
import com.jencys.books.repository.BookRepository;
import com.jencys.books.model.Book;
import com.jencys.books.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookDTO createBook(BookDTO bookDTO) {
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateISBNException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        Book savedBook = bookRepository.save(book);
        BookDTO savedBookDTO = new BookDTO();
        BeanUtils.copyProperties(savedBook, savedBookDTO);
        return savedBookDTO;
    }

    public BookDTO getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        BookDTO bookDTO = new BookDTO();
        BeanUtils.copyProperties(book, bookDTO);
        return bookDTO;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> {
                    BookDTO dto = new BookDTO();
                    BeanUtils.copyProperties(book, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        if (!existingBook.getIsbn().equals(bookDTO.getIsbn()) &&
                bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateISBNException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        BeanUtils.copyProperties(bookDTO, existingBook, "id");
        Book updatedBook = bookRepository.save(existingBook);
        BookDTO updatedBookDTO = new BookDTO();
        BeanUtils.copyProperties(updatedBook, updatedBookDTO);
        return updatedBookDTO;
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    public List<BookDTO> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(book -> {
                    BookDTO dto = new BookDTO();
                    BeanUtils.copyProperties(book, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<BookDTO> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(book -> {
                    BookDTO dto = new BookDTO();
                    BeanUtils.copyProperties(book, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}