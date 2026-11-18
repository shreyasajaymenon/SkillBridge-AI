package com.skillbridge.dto;

import java.util.List;

public record AllocationResponse(
        int totalEligibleShortlistEdges,
        int placements,
        int unmatchedStudents,
        int unfilledSeats,
        int topKPerInternship,
        String algorithm,
        String complexity,
        String optimizationNote,
        List<Placement> assignments,
        List<String> unmatchedStudentIds,
        List<String> internshipsWithVacancies
) {}
