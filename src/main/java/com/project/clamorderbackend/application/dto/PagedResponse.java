package com.project.clamorderbackend.application.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResponse<T> {

    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private List<T> content;
}
