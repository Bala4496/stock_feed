package ua.bala.stocks_feed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidApiKeyException extends ResponseStatusException {

    public InvalidApiKeyException() {
        super(HttpStatus.UNAUTHORIZED, "Request blocked Invalid ApiKey");
    }

    public InvalidApiKeyException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}
