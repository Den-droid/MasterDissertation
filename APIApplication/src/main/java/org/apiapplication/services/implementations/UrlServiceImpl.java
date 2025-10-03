package org.apiapplication.services.implementations;

import org.apiapplication.dto.url.MethodTypeDto;
import org.apiapplication.dto.url.UrlDto;
import org.apiapplication.entities.Url;
import org.apiapplication.enums.MethodType;
import org.apiapplication.exceptions.url.UrlWithNameNotFoundException;
import org.apiapplication.repositories.UrlRepository;
import org.apiapplication.services.interfaces.UrlService;
import org.apiapplication.utils.UrlMatcher;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class UrlServiceImpl implements UrlService {
    private UrlRepository urlRepository;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public List<UrlDto> get(String url, Integer method) {
        if ((url == null || url.isEmpty()) && method == null) {
            return getUrlDtoFromUrl(urlRepository.findAll());
        } else {
            List<Url> urls = urlRepository.findAll();
            Url neededUrl = null;

            urls = urls.stream()
                    .sorted((u1, u2) -> Comparator.comparing(Url::getUrl).compare(u1, u2))
                    .toList();

            for (Url urlToMatchWith : urls) {
                if (UrlMatcher.areMatched(urlToMatchWith.getUrl(), url)) {
                    neededUrl = urlToMatchWith;
                    break;
                }
            }

            if (neededUrl != null && (method != null && neededUrl.getMethod().ordinal() == method)) {
                return getUrlDtoFromUrl(List.of(neededUrl));
            } else {
                throw new UrlWithNameNotFoundException(url);
            }
        }

    }

    @Override
    public List<MethodTypeDto> getMethods() {
        return Arrays.stream(MethodType.values())
                .map(mt -> new MethodTypeDto(mt.ordinal(), mt.name()))
                .toList();
    }

    private List<UrlDto> getUrlDtoFromUrl(List<Url> urls) {
        return urls.stream()
                .map(u -> new UrlDto(u.getId(), u.getUrl(),
                        u.getDescription(), u.getMethod().ordinal()))
                .toList();
    }
}
