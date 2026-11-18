package com.skillbridge.controller;

import com.skillbridge.dto.*;
import com.skillbridge.model.Internship;
import com.skillbridge.model.Student;
import com.skillbridge.service.BenchmarkService;
import com.skillbridge.service.MatchingService;
import com.skillbridge.service.SampleDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final SampleDataService data;
    private final MatchingService matching;
    private final BenchmarkService benchmark;

    public ApiController(SampleDataService data, MatchingService matching, BenchmarkService benchmark) {
        this.data = data;
        this.matching = matching;
        this.benchmark = benchmark;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() { return matching.dashboard(); }

    @GetMapping("/students")
    public List<Student> students() { return data.students(); }

    @GetMapping("/internships")
    public List<Internship> internships() { return data.internships(); }

    @PostMapping("/matching/shortlist")
    public ShortlistResponse shortlist(@Valid @RequestBody ShortlistRequest request) {
        return matching.shortlist(request.internshipId(), request.topK());
    }

    @PostMapping("/matching/allocate")
    public AllocationResponse allocate(@Valid @RequestBody AllocationRequest request) {
        return matching.allocate(request.topKPerInternship());
    }

    @PostMapping("/benchmark")
    public Map<String, Object> benchmark() { return benchmark.run(); }
}
