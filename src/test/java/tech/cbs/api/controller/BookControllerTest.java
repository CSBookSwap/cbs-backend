package tech.cbs.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.cbs.api.service.BookService;
import tech.cbs.api.service.dto.BookDto;
import tech.cbs.api.service.dto.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDto bookDto;
    private Page page;

    private final String BASE_URL = "/api/v1/books";

    @BeforeEach
    public void init() {
        bookDto = new BookDto(100, "TestBook", "just book for test",  "", LocalDate.now(), new ArrayList<>(), new HashSet<>());
        page = new Page(0, 50);

    }

    @Test
    void BookController_CreateBook_ReturnCreated() throws Exception {
        given(bookService.createBook(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(bookDto.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(bookDto.description())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", CoreMatchers.is(bookDto.isbn())));
    }

//    @Test
//    void BookController_GetBooks_ReturnListOfBookDtos() {
//        List<BookDto> books = new ArrayList<>();2
//        when(bookService.getBooks(page)).thenReturn()
//
//    }
}