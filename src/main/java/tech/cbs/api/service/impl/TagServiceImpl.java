package tech.cbs.api.service.impl;

import org.springframework.stereotype.Service;
import tech.cbs.api.repository.TagRepository;
import tech.cbs.api.service.TagService;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
}
