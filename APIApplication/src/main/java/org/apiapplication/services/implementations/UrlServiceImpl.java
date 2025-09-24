package org.apiapplication.services.implementations;

import org.apiapplication.dto.url.UrlDto;
import org.apiapplication.entities.Url;
import org.apiapplication.exceptions.url.UrlWithNameNotFoundException;
import org.apiapplication.repositories.UrlRepository;
import org.apiapplication.services.interfaces.UrlService;
import org.apiapplication.utils.UrlMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlServiceImpl implements UrlService {
    private UrlRepository urlRepository;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public UrlDto getByName(String url) {
        List<Url> urls = urlRepository.findAll();
        Url neededUrl = null;

        for (Url urlToMatchWith : urls) {
            if (UrlMatcher.areMatched(urlToMatchWith.getUrl(), url)) {
                neededUrl = urlToMatchWith;
            }
        }

        if (neededUrl != null) {
            return new UrlDto(neededUrl.getId(), neededUrl.getUrl(),
                    neededUrl.getDescription(), neededUrl.getMethod());
        } else {
            throw new UrlWithNameNotFoundException(url);
        }
    }
}
