package com.skillbridge.model;

import java.util.List;

public record Student(
        String id,
        String name,
        double cgpa,
        List<String> skills,
        List<String> preferredInternshipIds
) {}
