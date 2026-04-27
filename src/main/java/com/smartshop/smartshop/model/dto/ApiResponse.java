package com.smartshop.smartshop.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime date;
    private String message;
    private int status;
    private T data;
    private String path;

    private PaginationDTO pagination = null;
}
