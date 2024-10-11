package com.hotel.booking.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class ErrorResponse implements Serializable {
    private int statusCode;
    private String message;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date timestamp;
}
