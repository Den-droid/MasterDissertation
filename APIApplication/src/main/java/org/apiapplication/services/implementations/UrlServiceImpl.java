package org.apiapplication.services.implementations;

import org.apiapplication.dto.url.MethodTypeDto;
import org.apiapplication.dto.url.UrlDto;
import org.apiapplication.entities.Url;
import org.apiapplication.entities.user.Role;
import org.apiapplication.entities.user.User;
import org.apiapplication.enums.MethodType;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.exceptions.url.UrlWithNameNotFoundException;
import org.apiapplication.repositories.UrlRepository;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.services.interfaces.UrlService;
import org.apiapplication.utils.UrlMatcher;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;

    private final SessionService sessionService;

    public UrlServiceImpl(UrlRepository urlRepository, SessionService sessionService) {
        this.urlRepository = urlRepository;
        this.sessionService = sessionService;
    }

    @Override
    public List<UrlDto> get(String url, Integer method) {
        User user = sessionService.getCurrentUser();
        if (user == null) {
            throw new PermissionException();
        }

        Role role = user.getRoles().get(0);

        if ((url == null || url.isEmpty()) && method == null) {
            List<UrlDto> urls = urlRepository.findAll().stream()
                    .filter(u -> u.getRoles().contains(role))
                    .map(this::getUrlDtoFromUrl)
                    .toList();
            return urls;
        } else {
            List<Url> urls = urlRepository.findAll();
            Url neededUrl = null;

            urls = urls.stream()
                    .filter(u -> u.getRoles().contains(role))
                    .sorted((u1, u2) -> Comparator.comparing(Url::getUrl).compare(u1, u2))
                    .toList();

            for (Url urlToMatchWith : urls) {
                if (UrlMatcher.areMatched(urlToMatchWith.getUrl(), url)
                        && urlToMatchWith.getMethod().ordinal() == method) {
                    neededUrl = urlToMatchWith;
                    break;
                }
            }

            if (neededUrl != null) {
                return List.of(getUrlDtoFromUrl(neededUrl));
            } else {
                throw new UrlWithNameNotFoundException(url,
                        Arrays.stream(MethodType.values()).filter(m -> m.ordinal() == method)
                                .findFirst().get().name());
            }
        }
    }

    @Override
    public List<MethodTypeDto> getMethods() {
        return Arrays.stream(MethodType.values())
                .map(mt -> new MethodTypeDto(mt.ordinal(), mt.name()))
                .toList();
    }

    private UrlDto getUrlDtoFromUrl(Url url) {
        return new UrlDto(url.getId(), url.getUrl(),
                url.getDescription(), new MethodTypeDto(url.getMethod().ordinal(), url.getMethod().name()));
    }
}
