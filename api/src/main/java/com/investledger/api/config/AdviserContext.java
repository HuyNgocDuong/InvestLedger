package com.investledger.api.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AdviserContext {
    public static final String ADVISER_HEADER = "X-Adviser-Id";

    private final Optional<Long> adviserId;

    public AdviserContext(HttpServletRequest request) {
        String header = request.getHeader(ADVISER_HEADER);
        if (header == null || header.isBlank()) {
            adviserId = Optional.empty();
        } else {
            adviserId = Optional.of(Long.parseLong(header.trim()));
        }
    }

    public Optional<Long> getAdviserId() {
        return adviserId;
    }
}
