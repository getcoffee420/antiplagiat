package getcoffee.antiplagiat.gateway.clients;

import getcoffee.antiplagiat.gateway.config.GatewayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AnalysisClient {

    private final RestClient client;

    @Autowired
    public AnalysisClient(RestClient.Builder builder, GatewayConfig cfg) {
        this.client = builder.baseUrl(cfg.analysisUrl()).build();
    }

    public record StartAnalysisRequest(String workId, String student, String task, String fileId, String sha256) {}

    public void startAnalysis(StartAnalysisRequest req) {
        client.post()
                .uri("/analysis")
                .body(req)
                .retrieve()
                .toBodilessEntity();
    }

    public record ReportDto(String reportId, boolean plagiarism) {}
    public record ReportsResponse(String workId, java.util.List<ReportDto> reports) {}

    public ReportsResponse getReports(String workId) {
        ReportsResponse resp = client.get()
                .uri("/works/{id}/reports", workId)
                .retrieve()
                .body(ReportsResponse.class);

        if (resp == null) {
            throw new IllegalStateException("Analysis returned empty response");
        }
        return resp;
    }
}
