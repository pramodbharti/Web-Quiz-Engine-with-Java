package engine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException() {
        super("quiz not found");
    }
}
