package tech.cbs.api.service;

import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.dto.TagDto;

import java.util.List;

public interface TagService {
    List<TagDto> getTags(Page page);

    TagDto getTag(int id);

    TagDto createTag(TagDto tagDto);

    boolean updateTag(TagDto tagDto);

    boolean deleteTag(int id);
}
