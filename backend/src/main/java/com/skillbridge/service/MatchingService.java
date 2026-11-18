package com.skillbridge.service;

import com.skillbridge.algorithm.EdmondsKarp;
import com.skillbridge.algorithm.RandomizedQuickselect;
import com.skillbridge.dto.*;
import com.skillbridge.model.Internship;
import com.skillbridge.model.Student;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
public class MatchingService {
    private static final Comparator<CandidateScore> RANKING =
            Comparator.comparingDouble(CandidateScore::score).reversed()
                    .thenComparing(c -> c.student().id());

    private final SampleDataService data;

    public MatchingService(SampleDataService data) {
        this.data = data;
    }

    public ShortlistResponse shortlist(String internshipId, int topK) {
        if (topK < 1) throw new IllegalArgumentException("topK must be at least 1");
        Internship internship = findInternship(internshipId);
        List<CandidateScore> eligible = eligibleScores(internship);
        int count = Math.min(topK, eligible.size());

        if (count == 0) {
            return new ShortlistResponse(internshipId, 0, 0, null,
                    "Randomized Quickselect", "Expected O(n), worst O(n^2)", List.of());
        }

        List<CandidateScore> work = new ArrayList<>(eligible);
        double threshold = RandomizedQuickselect.selectKthLargest(
                work, count, CandidateScore::score);

        // Keep all scores above the cutoff, then fill cutoff ties deterministically.
        List<CandidateScore> selected = eligible.stream()
                .filter(candidate -> Double.compare(candidate.score(), threshold) > 0)
                .collect(Collectors.toCollection(ArrayList::new));

        List<CandidateScore> ties = eligible.stream()
                .filter(candidate -> Double.compare(candidate.score(), threshold) == 0)
                .sorted(Comparator.comparing(c -> c.student().id()))
                .toList();

        for (CandidateScore tie : ties) {
            if (selected.size() >= count) break;
            selected.add(tie);
        }
        selected.sort(RANKING);

        return new ShortlistResponse(internshipId, eligible.size(), selected.size(),
                threshold, "Randomized Quickselect",
                "Expected O(n) selection; worst O(n^2); O(k log k) to order shortlist",
                selected);
    }

    public AllocationResponse allocate(int topKPerInternship) {
        if (topKPerInternship < 1) {
            throw new IllegalArgumentException("topKPerInternship must be at least 1");
        }

        Map<String, List<CandidateScore>> shortlistByInternship = new LinkedHashMap<>();
        Set<String> shortlistedStudentIds = new TreeSet<>();
        int edgeCount = 0;

        for (Internship internship : data.internships()) {
            ShortlistResponse response = shortlist(internship.id(), topKPerInternship);
            shortlistByInternship.put(internship.id(), response.candidates());
            for (CandidateScore candidate : response.candidates()) {
                shortlistedStudentIds.add(candidate.student().id());
                edgeCount++;
            }
        }

        List<String> studentIds = new ArrayList<>(shortlistedStudentIds);
        Map<String, Integer> studentNode = new HashMap<>();
        Map<String, Integer> internshipNode = new HashMap<>();

        int source = 0;
        int firstStudentNode = 1;
        int firstInternshipNode = firstStudentNode + studentIds.size();
        int sink = firstInternshipNode + data.internships().size();
        EdmondsKarp flow = new EdmondsKarp(sink + 1);

        for (int i = 0; i < studentIds.size(); i++) {
            String id = studentIds.get(i);
            studentNode.put(id, firstStudentNode + i);
            flow.addEdge(source, firstStudentNode + i, 1);
        }

        for (int i = 0; i < data.internships().size(); i++) {
            Internship internship = data.internships().get(i);
            int node = firstInternshipNode + i;
            internshipNode.put(internship.id(), node);
            flow.addEdge(node, sink, internship.seats());
        }

        Map<EdmondsKarp.Edge, CandidateScore> assignmentEdges = new IdentityHashMap<>();
        for (Internship internship : data.internships()) {
            int jobNode = internshipNode.get(internship.id());
            for (CandidateScore candidate : shortlistByInternship.get(internship.id())) {
                int personNode = studentNode.get(candidate.student().id());
                EdmondsKarp.Edge edge = flow.addEdge(personNode, jobNode, 1);
                assignmentEdges.put(edge, candidate);
            }
        }

        int placements = flow.maxFlow(source, sink);
        List<Placement> assignments = new ArrayList<>();
        Set<String> assignedStudents = new HashSet<>();

        for (Map.Entry<EdmondsKarp.Edge, CandidateScore> entry : assignmentEdges.entrySet()) {
            if (entry.getKey().flow() == 1) {
                CandidateScore candidate = entry.getValue();
                assignments.add(new Placement(
                        candidate.student().id(), candidate.student().name(),
                        candidate.internshipId(), candidate.internshipTitle(),
                        candidate.company(), candidate.score()));
                assignedStudents.add(candidate.student().id());
            }
        }
        assignments.sort(Comparator.comparing(Placement::internshipId)
                .thenComparing(Placement::studentId));

        // Count every demo student not assigned, including students who did not
        // qualify for any role or did not enter a top-k shortlist.
        List<String> unmatched = data.students().stream()
                .map(Student::id)
                .filter(id -> !assignedStudents.contains(id))
                .sorted()
                .toList();

        int totalSeats = data.internships().stream().mapToInt(Internship::seats).sum();
        int unfilledSeats = totalSeats - placements;
        List<String> vacancies = data.internships().stream()
                .filter(job -> assignments.stream()
                        .filter(a -> a.internshipId().equals(job.id())).count() < job.seats())
                .map(Internship::id).toList();

        return new AllocationResponse(edgeCount, placements, unmatched.size(), unfilledSeats,
                topKPerInternship, "Edmonds–Karp maximum flow",
                "O(VE^2)",
                "Maximizes placement count within the per-internship Quickselect shortlists; it does not maximize total compatibility score.",
                assignments, unmatched, vacancies);
    }

    public DashboardResponse dashboard() {
        return new DashboardResponse(data.students().size(), data.internships().size(),
                data.internships().stream().mapToInt(Internship::seats).sum(),
                "SkillBridge AI", "Demo data loaded");
    }

    private Internship findInternship(String id) {
        return data.internships().stream().filter(job -> job.id().equals(id)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Internship not found: " + id));
    }

    private List<CandidateScore> eligibleScores(Internship internship) {
        return data.students().stream()
                .filter(student -> student.cgpa() >= internship.minimumCgpa())
                .map(student -> score(student, internship))
                .sorted(Comparator.comparing(c -> c.student().id()))
                .toList();
    }

    private CandidateScore score(Student student, Internship internship) {
        Set<String> studentSkills = student.skills().stream()
                .map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
        long matched = internship.requiredSkills().stream()
                .filter(skill -> studentSkills.contains(skill.toLowerCase(Locale.ROOT))).count();
        double skillScore = internship.requiredSkills().isEmpty()
                ? 70.0 : 70.0 * matched / internship.requiredSkills().size();
        double preferenceScore = student.preferredInternshipIds().contains(internship.id())
                ? 20.0 : 0.0;
        double academicScore = Math.min(10.0, Math.max(0.0, student.cgpa()));
        double total = Math.min(100.0, skillScore + preferenceScore + academicScore);
        return new CandidateScore(student, internship.id(), internship.title(),
                internship.company(), round(total), round(skillScore),
                preferenceScore, round(academicScore));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
