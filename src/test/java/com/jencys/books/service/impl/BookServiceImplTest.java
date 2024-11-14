package com.jencys.books.service.impl;

import com.jencys.books.dto.BookDTO;
import com.jencys.books.exception.BookNotFoundException;
import com.jencys.books.exception.DuplicateISBNException;
import com.jencys.books.repository.BookRepository;
import com.jencys.books.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setPublicationYear(2024);
        testBook.setDescription("Test Description");

        testBookDTO = new BookDTO();
        testBookDTO.setId(1L);
        testBookDTO.setTitle("Test Book");
        testBookDTO.setAuthor("Test Author");
        testBookDTO.setIsbn("1234567890");
        testBookDTO.setPublicationYear(2024);
        testBookDTO.setDescription("Test Description");
    }

    @Test
    void createBook_Success() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookDTO result = bookService.createBook(testBookDTO);

        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        assertEquals(testBookDTO.getAuthor(), result.getAuthor());
        assertEquals(testBookDTO.getIsbn(), result.getIsbn());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_DuplicateISBN_ThrowsException() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        assertThrows(DuplicateISBNException.class, () ->
                bookService.createBook(testBookDTO)
        );
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        BookDTO result = bookService.getBook(1L);

        assertNotNull(result);
        assertEquals(testBookDTO.getId(), result.getId());
        assertEquals(testBookDTO.getTitle(), result.getTitle());
    }

    @Test
    void getBook_NotFound_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () ->
                bookService.getBook(1L)
        );
    }

    @Test
    void getAllBooks_Success() {
        List<Book> books = Collections.singletonList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        List<BookDTO> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBookDTO.getTitle(), result.get(0).getTitle());
    }

    @Test
    void updateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookDTO result = bookService.updateBook(1L, testBookDTO);

        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_NotFound_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () ->
                bookService.updateBook(1L, testBookDTO)
        );
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_DuplicateISBN_ThrowsException() {
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setIsbn("987654321");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.existsByIsbn(testBookDTO.getIsbn())).thenReturn(true);

        assertThrows(DuplicateISBNException.class, () ->
                bookService.updateBook(1L, testBookDTO)
        );
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_NotFound_ThrowsException() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () ->
                bookService.deleteBook(1L)
        );
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchBooksByAuthor_Success() {
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByAuthorContainingIgnoreCase(anyString())).thenReturn(books);

        List<BookDTO> result = bookService.searchBooksByAuthor("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBookDTO.getAuthor(), result.get(0).getAuthor());
    }

    @Test
    void searchBooksByTitle_Success() {
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByTitleContainingIgnoreCase(anyString())).thenReturn(books);

        List<BookDTO> result = bookService.searchBooksByTitle("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBookDTO.getTitle(), result.get(0).getTitle());
    }
}
