package tech.cbs.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.cbs.api.service.AuthorService;
import tech.cbs.api.service.dto.AuthorDto;
import tech.cbs.api.service.dto.Page;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    private final static int authorsCount = 25;
    @Mock
    private AuthorService authorService;
    @InjectMocks
    private AuthorController authorController;
    private MockMvc mockMvc;

    private static List<AuthorDto> createAuthors() {

        var authors = new ArrayList<AuthorDto>();

        for (int i = 0; i < authorsCount; i++) {
            authors.add(new AuthorDto(i, "Author #" + i, "Biography #" + i));
        }
        return authors;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
    }

    @Test
    void AuthorController_GetAuthors_ReturnsListOfAuthors() throws Exception {
        var authors = createAuthors();
        doReturn(authors).when(this.authorService).getAuthors(new Page(0, authorsCount));

        var responseEntity = this.authorController.getAuthors(0, authorsCount);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(authors);
    }

    @Test
    void AuthorController_GetAuthor_ReturnsAuthor() throws Exception {
        var author = new AuthorDto(1, "Author #1", "Biography #1");
        doReturn(author).when(this.authorService).getAuthor(1);

        var responseEntity = this.authorController.getAuthor(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(author);
    }

    @Test
    void AuthorController_CreateAuthor_ReturnsAuthor() throws Exception {
        var author = new AuthorDto(1, "Author #1", "Biography #1");
        doReturn(author).when(this.authorService).createAuthor(author);

        var responseEntity = this.authorController.createAuthor(author);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(author);
    }

    @Test
    void AuthorController_UpdateAuthor_ReturnResult() throws Exception {
        var author = new AuthorDto(1, "Author #1", "Biography #1");
        doReturn(true).when(this.authorService).updateAuthor(author);

        var responseEntity = this.authorController.updateAuthor(author);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
    }

    @Test
    void AuthorController_DeleteAuthor_ReturnResult() throws Exception {
        doReturn(true).when(this.authorService).deleteAuthor(1);

        var responseEntity = this.authorController.deleteAuthor(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
    }
}
