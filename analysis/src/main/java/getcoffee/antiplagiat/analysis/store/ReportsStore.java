package getcoffee.antiplagiat.analysis.store;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReportsStore {

    public record Submission(String workId, String student, String task, String hash, Instant createdAt) {}
    public record Report(String reportId, boolean plagiarism) {}

    private final List<Submission> submissions = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<Report>> reportsByWork = new ConcurrentHashMap<>();

    public void analyze(String workId, String student, String task, String hash) {
        boolean plagiarism = submissions.stream()
                .anyMatch(s -> s.hash().equals(hash) && !s.student().equals(student));

        submissions.add(new Submission(workId, student, task, hash, Instant.now()));
        reportsByWork.put(workId, List.of(new Report("r-" + UUID.randomUUID(), plagiarism)));
    }

    public List<Report> getReports(String workId) {
        return reportsByWork.getOrDefault(workId, List.of());
    }
}
