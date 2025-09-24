package org.apiapplication.services.implementations;

import org.apiapplication.dto.field.FieldDto;
import org.apiapplication.entities.Field;
import org.apiapplication.entities.Url;
import org.apiapplication.exceptions.url.UrlWithNameNotFoundException;
import org.apiapplication.repositories.FieldRepository;
import org.apiapplication.repositories.UrlRepository;
import org.apiapplication.services.interfaces.FieldService;
import org.apiapplication.utils.UrlMatcher;
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
    public List<FieldDto> getByUrl(String url) {
        List<Url> urls = urlRepository.findAll();
        Url neededUrl = null;

        for (Url urlToMatchWith : urls) {
            if (UrlMatcher.areMatched(urlToMatchWith.getUrl(), url)) {
                neededUrl = urlToMatchWith;
            }
        }

        if (neededUrl != null) {
            List<Field> fields = neededUrl.getFields();
            List<FieldDto> fieldDtos = fields.stream()
                    .map(f -> new FieldDto(f.getId(), f.getName(), f.getLabel(),
                            f.getDescription(), f.getType(), f.isRequired()))
                    .toList();
            return fieldDtos;
        } else {
            throw new UrlWithNameNotFoundException(url);
        }
    }
}
