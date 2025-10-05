package org.apiapplication.services.implementations;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.field.FieldDto;
import org.apiapplication.entities.Url;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.FieldRepository;
import org.apiapplication.repositories.UrlRepository;
import org.apiapplication.services.interfaces.FieldService;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldServiceImpl implements FieldService {
    private final FieldRepository fieldRepository;
    private final UrlRepository urlRepository;

    private final SessionService sessionService;

    public FieldServiceImpl(FieldRepository fieldRepository,
                            UrlRepository urlRepository,
                            SessionService sessionService) {
        this.fieldRepository = fieldRepository;
        this.urlRepository = urlRepository;

        this.sessionService = sessionService;
    }

    @Override
    public List<FieldDto> getByUrlId(Integer urlId) {
        if (sessionService.getCurrentUser() == null)
            throw new PermissionException();

        Url url = urlRepository.findById(urlId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.URL, urlId.toString()));

        List<FieldDto> fieldDtos = url.getUrlFields().stream()
                .map(f -> new FieldDto(f.getField().getId(),
                        f.getField().getName(), f.getField().getLabel(),
                        f.getField().getDescription(),
                        f.getField().getType().ordinal(),
                        f.isRequired()))
                .toList();

        return fieldDtos;
    }
}
