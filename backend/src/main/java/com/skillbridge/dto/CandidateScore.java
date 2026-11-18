package com.skillbridge.dto;

import com.skillbridge.model.Student;

public record CandidateScore(
        Student student,
        String internshipId,
        String internshipTitle,
        String company,
        double score,
        double skillScore,
        double preferenceScore,
        double academicScore
) {}
