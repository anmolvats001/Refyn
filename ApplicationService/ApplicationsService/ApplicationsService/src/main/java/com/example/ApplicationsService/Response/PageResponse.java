package com.example.ApplicationsService.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class PageResponse {
    List<ApplicationResponse> applications;
    int currentPage;
    int totalPages;
    int pageSize;
    long totalElements;
}
