package com.skillbridge.model;

import java.util.List;

public record Internship(
        String id,
        String company,
        String title,
        List<String> requiredSkills,
        double minimumCgpa,
        int seats
) {}
