package tech.cbs.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.cbs.api.service.TagService;
import tech.cbs.api.service.dto.Page;
import tech.cbs.api.service.dto.TagDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/{num}/{size}")
    public ResponseEntity<List<TagDto>> getTags(
            @PathVariable("num") int num,
            @PathVariable("size") int size
    ) {
        return ResponseEntity.ok(tagService.getTags(new Page(num, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTag(@PathVariable("id") int id) {
        return ResponseEntity.ok(tagService.getTag(id));
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag(TagDto tagDto) {
        return ResponseEntity.ok(tagService.createTag(tagDto));
    }

    @PutMapping
    public ResponseEntity<Boolean> updateTag(TagDto tagDto) {
        return ResponseEntity.ok(tagService.updateTag(tagDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteTag(@PathVariable("id") int id) {
        return ResponseEntity.ok(tagService.deleteTag(id));
    }
}
