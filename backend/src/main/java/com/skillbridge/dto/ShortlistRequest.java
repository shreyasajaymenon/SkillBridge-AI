package com.skillbridge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ShortlistRequest(
        @NotBlank String internshipId,
        @Min(1) int topK
) {}
