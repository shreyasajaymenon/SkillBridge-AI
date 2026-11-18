package com.skillbridge.dto;

import java.util.List;

public record ShortlistResponse(
        String internshipId,
        int eligibleCandidates,
        int returnedCandidates,
        Double cutoffScore,
        String algorithm,
        String complexity,
        List<CandidateScore> candidates
) {}
