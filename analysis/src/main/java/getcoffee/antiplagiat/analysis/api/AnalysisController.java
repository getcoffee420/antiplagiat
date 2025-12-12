package getcoffee.antiplagiat.analysis.api;

import getcoffee.antiplagiat.analysis.store.ReportsStore;
import org.springframework.web.bind.annotation.*;

@RestController
public class AnalysisController {

    private final ReportsStore store;

    public AnalysisController(ReportsStore store) {
        this.store = store;
    }

    public record StartAnalysisRequest(String workId, String student, String task, String fileId, String hash) {}

    @PostMapping("/analysis")
    public void start(@RequestBody StartAnalysisRequest req) {
        store.analyze(req.workId(), req.student(), req.task(), req.hash());
    }
}
