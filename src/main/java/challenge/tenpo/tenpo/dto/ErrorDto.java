package challenge.tenpo.tenpo.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ErrorDto {
    private Instant timestamp;
    private int status;
    private String message;

    public ErrorDto(Instant timestamp, int status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

}

