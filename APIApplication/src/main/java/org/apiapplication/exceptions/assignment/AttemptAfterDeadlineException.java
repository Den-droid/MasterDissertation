package org.apiapplication.exceptions.assignment;

import java.time.LocalDateTime;

public class AttemptAfterDeadlineException extends RuntimeException {
    public AttemptAfterDeadlineException(LocalDateTime deadline) {
        super(String.format("Відповідь не зарахована, оскільки відправлена після дедлайну: %s.", deadline.toString()));
    }
}
