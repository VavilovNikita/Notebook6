package ru.vavilov.notebook6.notebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vavilov.notebook6.notebook.entity.Tag;
import ru.vavilov.notebook6.notebook.repository.TagRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc();
    }

    /**
     * Parses a comma-separated list of tag names, reusing existing tags (case-insensitive)
     * and creating new ones as needed.
     */
    @Transactional
    public Set<Tag> resolveOrCreate(String commaSeparatedNames) {
        Set<Tag> result = new HashSet<>();
        if (commaSeparatedNames == null || commaSeparatedNames.isBlank()) {
            return result;
        }
        for (String rawName : commaSeparatedNames.split(",")) {
            String name = rawName.trim();
            if (name.isEmpty()) {
                continue;
            }
            Tag tag = tagRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name)));
            result.add(tag);
        }
        return result;
    }
}
