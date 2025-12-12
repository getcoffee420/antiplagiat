package getcoffee.antiplagiat.analysis.api;

import getcoffee.antiplagiat.analysis.store.ReportsStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/works")
public class ReportsController {

    private final ReportsStore store;

    public ReportsController(ReportsStore store) {
        this.store = store;
    }

    public record ReportDto(String reportId, boolean plagiarism) {}
    public record ReportsResponse(String workId, List<ReportDto> reports) {}

    @GetMapping("/{workId}/reports")
    public ReportsResponse get(@PathVariable String workId) {
        var reports = store.getReports(workId).stream()
                .map(r -> new ReportDto(r.reportId(), r.plagiarism()))
                .toList();
        return new ReportsResponse(workId, reports);
    }
}
