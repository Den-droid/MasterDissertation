package org.apiapplication.services.implementations;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.field.FieldDto;
import org.apiapplication.dto.url.UrlDto;
import org.apiapplication.entities.Url;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.repositories.FieldRepository;
import org.apiapplication.repositories.UrlRepository;
import org.apiapplication.services.interfaces.FieldService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldServiceImpl implements FieldService {
    private FieldRepository fieldRepository;
    private UrlRepository urlRepository;

    public FieldServiceImpl(FieldRepository fieldRepository, UrlRepository urlRepository) {
        this.fieldRepository = fieldRepository;
        this.urlRepository = urlRepository;
    }

    @Override
    public List<FieldDto> getByUrlId(Integer urlId) {
        Url url = urlRepository.findById(urlId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.URL, urlId));
        List<FieldDto> fieldDtos = url.getFields().stream()
                .map(f -> new FieldDto(f.getId(), f.getName(), f.getLabel(),
                        f.getDescription(), f.getType().ordinal(), f.isRequired()))
                .toList();

        return fieldDtos;
    }
}
