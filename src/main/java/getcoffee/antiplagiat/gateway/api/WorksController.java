package getcoffee.antiplagiat.gateway.api;

import getcoffee.antiplagiat.gateway.clients.AnalysisClient;
import getcoffee.antiplagiat.gateway.clients.StorageClient;
import getcoffee.antiplagiat.gateway.util.Hashing;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/works")
public class WorksController {

    private final StorageClient storage;
    private final AnalysisClient analysis;

    public WorksController(StorageClient storage, AnalysisClient analysis) {
        this.storage = storage;
        this.analysis = analysis;
    }

    public record CreateWorkResponse(String workId, String fileId) {}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateWorkResponse createWork(
            @RequestPart("file") MultipartFile file,
            @RequestPart("student") String student,
            @RequestPart("task") String task
    ) throws Exception {

        byte[] bytes = file.getBytes();
        String sha256 = Hashing.hash(bytes);

        String fileId = storage.upload(file);

        String workId = "w-" + UUID.randomUUID();

        analysis.startAnalysis(new AnalysisClient.StartAnalysisRequest(
                workId,
                student,
                task,
                fileId,
                sha256
        ));

        return new CreateWorkResponse(workId, fileId);
    }

    @GetMapping("/{workId}/reports")
    public AnalysisClient.ReportsResponse getReports(@PathVariable String workId) {
        return analysis.getReports(workId);
    }
}
