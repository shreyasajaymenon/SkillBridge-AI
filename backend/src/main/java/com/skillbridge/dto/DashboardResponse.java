package com.skillbridge.dto;

public record DashboardResponse(
        int students,
        int internships,
        int totalSeats,
        String project,
        String status
) {}
