package gympie.springboot.a_simple_login.dto;

import java.time.Instant;
import java.util.Map;

public record ApiError(Instant timestamp, int status, String error, String message,
                       Map<String, String> fieldErrors) {
}
