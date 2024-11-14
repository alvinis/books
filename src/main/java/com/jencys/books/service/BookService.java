package com.jencys.books.service;

import com.jencys.books.dto.BookDTO;

import java.util.List;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    BookDTO getBook(Long id);
    List<BookDTO> getAllBooks();
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
    List<BookDTO> searchBooksByAuthor(String author);
    List<BookDTO> searchBooksByTitle(String title);
}
