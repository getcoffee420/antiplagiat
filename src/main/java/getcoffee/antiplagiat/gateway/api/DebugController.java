package getcoffee.antiplagiat.gateway.api;

import getcoffee.antiplagiat.gateway.clients.AnalysisClient;
import getcoffee.antiplagiat.gateway.clients.StorageClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final StorageClient storage;
    private final AnalysisClient analysis;

    public DebugController(StorageClient storage, AnalysisClient analysis) {
        this.storage = storage;
        this.analysis = analysis;
    }

    public record UploadResult(String fileId) {}

    @PostMapping(value = "/upload-to-storage", consumes = "multipart/form-data")
    public UploadResult uploadToStorage(@RequestPart("file") MultipartFile file) throws Exception {
        String fileId = storage.upload(file);
        return new UploadResult(fileId);
    }

    // 1) дергаем analysis start
    @PostMapping("/start-analysis")
    public void startAnalysis(@RequestBody AnalysisClient.StartAnalysisRequest req) {
        analysis.startAnalysis(req);
    }

    // 2) дергаем analysis reports
    @GetMapping("/works/{workId}/reports")
    public AnalysisClient.ReportsResponse reports(@PathVariable String workId) {
        return analysis.getReports(workId);
    }
}
