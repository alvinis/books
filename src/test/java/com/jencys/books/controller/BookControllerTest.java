package com.jencys.books.controller;

import com.jencys.books.dto.BookDTO;
import com.jencys.books.exception.BookNotFoundException;
import com.jencys.books.exception.DuplicateISBNException;
import com.jencys.books.exception.GlobalExceptionHandler;
import com.jencys.books.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookServiceImpl bookService;

    @InjectMocks
    private BookController bookController;

    private ObjectMapper objectMapper;
    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testBookDTO = new BookDTO();
        testBookDTO.setId(1L);
        testBookDTO.setTitle("Test Book");
        testBookDTO.setAuthor("Test Author");
        testBookDTO.setIsbn("1234567890");
        testBookDTO.setPublicationYear(2024);
        testBookDTO.setDescription("Test Description");
    }

    @Test
    void createBook_Success() throws Exception {
        when(bookService.createBook(any(BookDTO.class))).thenReturn(testBookDTO);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(testBookDTO.getTitle()))
                .andExpect(jsonPath("$.author").value(testBookDTO.getAuthor()));
    }

    @Test
    void createBook_InvalidInput() throws Exception {
        testBookDTO.setTitle(""); // Invalid title

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBook_DuplicateISBN() throws Exception {
        when(bookService.createBook(any(BookDTO.class)))
                .thenThrow(new DuplicateISBNException("Duplicate ISBN"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void getBook_Success() throws Exception {
        when(bookService.getBook(1L)).thenReturn(testBookDTO);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testBookDTO.getTitle()))
                .andExpect(jsonPath("$.author").value(testBookDTO.getAuthor()));
    }

    @Test
    void getBook_NotFound() throws Exception {
        when(bookService.getBook(1L)).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBooks_Success() throws Exception {
        List<BookDTO> books = Arrays.asList(testBookDTO);
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(testBookDTO.getTitle()))
                .andExpect(jsonPath("$[0].author").value(testBookDTO.getAuthor()));
    }

    @Test
    void updateBook_Success() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookDTO.class))).thenReturn(testBookDTO);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testBookDTO.getTitle()));
    }

    @Test
    void updateBook_NotFound() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookDTO.class)))
                .thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_Success() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_NotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found"))
                .when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchBooksByAuthor_Success() throws Exception {
        List<BookDTO> books = Arrays.asList(testBookDTO);
        when(bookService.searchBooksByAuthor(anyString())).thenReturn(books);

        mockMvc.perform(get("/api/books/search/author")
                        .param("author", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value(testBookDTO.getAuthor()));
    }

    @Test
    void searchBooksByTitle_Success() throws Exception {
        List<BookDTO> books = Arrays.asList(testBookDTO);
        when(bookService.searchBooksByTitle(anyString())).thenReturn(books);

        mockMvc.perform(get("/api/books/search/title")
                        .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(testBookDTO.getTitle()));
    }
}