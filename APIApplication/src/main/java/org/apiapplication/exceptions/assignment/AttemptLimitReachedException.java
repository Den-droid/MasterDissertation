package org.apiapplication.exceptions.assignment;

import java.time.LocalDateTime;

public class AttemptLimitReachedException extends RuntimeException {
    public AttemptLimitReachedException(LocalDateTime nextAttemptTime) {
        super(String.format("Відповідь не зарахована, оскільки досягенено ліміту відповідей. " +
                "Наступна спроба може бути зроблена в %s.", nextAttemptTime.toString()));
    }
}
