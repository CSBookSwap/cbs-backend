package tech.cbs.api.service.impl;

import org.springframework.stereotype.Service;
import tech.cbs.api.exception.ResourceNotFoundException;
import tech.cbs.api.repository.TagRepository;
import tech.cbs.api.service.TagService;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.dto.TagDto;
import tech.cbs.api.service.mapper.TagMapper;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
        this.tagMapper = new TagMapper();
    }

    @Override
    public List<TagDto> getTags(Page page) {
        return tagRepository.findAll(page)
                .stream()
                .map(tagMapper)
                .toList();
    }

    @Override
    public TagDto getTag(int id) {
        return tagRepository.findById(id)
                .map(tagMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find tag with id " + id));
    }

    @Override
    public TagDto createTag(TagDto tagDto) {

        var tag = TagMapper.toModel(tagDto);
        int tagId = tagRepository.save(tag);

        return tagRepository.findById(tagId)
                .map(tagMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Tag cannot be created."));
    }

    @Override
    public boolean updateTag(TagDto tagDto) {
        return tagRepository.update(TagMapper.toModel(tagDto));
    }

    @Override
    public boolean deleteTag(int id) {
        return tagRepository.deleteById(id);
    }
}
