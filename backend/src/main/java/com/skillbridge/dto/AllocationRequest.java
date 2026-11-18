package com.skillbridge.dto;

import jakarta.validation.constraints.Min;

public record AllocationRequest(@Min(1) int topKPerInternship) {}
