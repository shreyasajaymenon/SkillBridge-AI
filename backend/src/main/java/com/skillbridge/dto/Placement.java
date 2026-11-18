package com.skillbridge.dto;

public record Placement(
        String studentId,
        String studentName,
        String internshipId,
        String internshipTitle,
        String company,
        double compatibilityScore
) {}
