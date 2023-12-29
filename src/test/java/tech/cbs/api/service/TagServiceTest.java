package tech.cbs.api.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.cbs.api.exception.ResourceNotFoundException;
import tech.cbs.api.repository.TagRepository;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.dto.TagDto;
import tech.cbs.api.service.mapper.TagMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link TagService}
 */
@SpringBootTest
@Testcontainers
class TagServiceTest {


    private static final List<TagDto> testTags = new ArrayList<>();
    private static final int tagCount = 42;
    private static final Random rn = new Random();
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0-alpine3.18");
    @Autowired
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private NamedParameterJdbcTemplate parameterJdbcTemplate;

    @BeforeEach
    void setData() {

        Map<String, TagDto> tags = new HashMap();

        for (int i = 0; i <= tagCount; i++) {
            var tag = new TagDto(0, "Tag name # " + rn.nextInt());
            tags.put(tag.name(), tag);
        }

        tags.values().stream()
                .map(TagMapper::toModel)
                .map(tagRepository::save)
                .map(tagRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TagMapper::toDto)
                .forEach(testTags::add);
    }

    @AfterEach
    void clearData() {
        parameterJdbcTemplate.update("DELETE FROM tag;", Map.of());
        testTags.clear();
    }

    @Test
    void TestService_GetTags_ReturnListOfTagDto() {
        List<TagDto> tags = tagService.getTags(new Page(0, tagCount));
        assertThat(tags).isNotNull();
        assertThat(tags).isNotEmpty();
        assertThat(tags.size()).isEqualTo(tagCount);
    }

    @Test
    void TagService_GetTag_ReturnTagDto() {

        TagDto testTag = testTags.get(rn.nextInt(0, testTags.size() - 1));

        TagDto tag = tagService.getTag(testTag.id());

        assertThat(tag).isNotNull();
        assertThat(tag).isEqualTo(testTag);
    }

    @Test
    void TagService_CreateTag_ReturnTagDto() {
        TagDto tag = new TagDto(0, "Tag name # " + rn.nextInt());
        TagDto createdTag = tagService.createTag(tag);

        assertThat(createdTag).isNotNull();
        assertThat(createdTag.id()).isNotZero();
        assertThat(createdTag.name()).isEqualTo(tag.name());
    }

    @Test
    void TagService_UpdateTag_ReturnResultAsBoolean() {
        TagDto testTag = testTags.get(rn.nextInt(0, testTags.size() - 1));
        TagDto tag = new TagDto(testTag.id(), "Tag name # " + rn.nextInt());

        boolean result = tagService.updateTag(tag);

        assertThat(result).isTrue();
    }

    @Test
    void TagService_DeleteTag_ReturnResultAsBoolean() {
        TagDto testTag = testTags.get(rn.nextInt(0, testTags.size() - 1));

        int tagId = testTag.id();

        boolean result = tagService.deleteTag(tagId);

        assertThat(result).isTrue();

        Throwable thrown = assertThrows(ResourceNotFoundException.class, () -> tagService.getTag(tagId));
        assertThat(thrown.getMessage()).isNotNull();
    }
}