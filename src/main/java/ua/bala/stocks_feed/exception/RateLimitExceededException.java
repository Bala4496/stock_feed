package ua.bala.stocks_feed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RateLimitExceededException extends ResponseStatusException {

    public RateLimitExceededException() {
        super(HttpStatus.TOO_MANY_REQUESTS, "Request blocked due to rate limiting");
    }

    public RateLimitExceededException(String reason) {
        super(HttpStatus.TOO_MANY_REQUESTS, reason);
    }
}
