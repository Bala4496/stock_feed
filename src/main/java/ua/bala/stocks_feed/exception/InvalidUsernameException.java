package ua.bala.stocks_feed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidUsernameException extends ResponseStatusException {

    public InvalidUsernameException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
