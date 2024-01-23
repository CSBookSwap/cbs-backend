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
import tech.cbs.api.service.TagService;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.dto.TagDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {


    private final static int tagsCount = 25;
    @Mock
    private TagService tagService;
    @InjectMocks
    private TagController tagController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
    }

    private List<TagDto> createTags() {
        List<TagDto> tags = new ArrayList<>();
        for (int i = 0; i < tagsCount; i++) {
            tags.add(new TagDto(i, "Tag #" + i));
        }
        return tags;
    }

    @Test
    void TagController_GetTags_ReturnsListOfTags() throws Exception {
        var tags = createTags();
        doReturn(tags).when(this.tagService).getTags(new Page(0, tagsCount));

        var responseEntity = this.tagController.getTags(0, tagsCount);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(tags);
    }

    @Test
    void TagController_GetTag_ReturnsTag() throws Exception {
        var tag = new TagDto(1, "Tag #1");
        doReturn(tag).when(this.tagService).getTag(1);

        var responseEntity = this.tagController.getTag(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(tag);
    }

    @Test
    void TagController_CreateTag_ReturnsTag() throws Exception {
        var tag = new TagDto(1, "Tag #1");
        doReturn(tag).when(this.tagService).createTag(tag);

        var responseEntity = this.tagController.createTag(tag);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(tag);
    }

    @Test
    void TagController_UpdateTag_ReturnResult() throws Exception {
        var tag = new TagDto(1, "Tag #1");
        doReturn(true).when(this.tagService).updateTag(tag);

        var responseEntity = this.tagController.updateTag(tag);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
    }

    @Test
    void TagController_DeleteTag_ReturnResult() throws Exception {
        doReturn(true).when(this.tagService).deleteTag(1);

        var responseEntity = this.tagController.deleteTag(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();
    }
}