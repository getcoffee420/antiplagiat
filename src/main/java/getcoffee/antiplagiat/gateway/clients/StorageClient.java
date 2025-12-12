package getcoffee.antiplagiat.gateway.clients;

import getcoffee.antiplagiat.gateway.config.GatewayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Component
public class StorageClient {

    private final RestClient client;

    @Autowired
    public StorageClient(RestClient.Builder builder, GatewayConfig cfg) {
        this.client = builder.baseUrl(cfg.storageUrl()).build();
    }

    public record FileUploadResponse(String fileId) {}

    public String upload(MultipartFile file) throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        body.add("file", resource);

        FileUploadResponse resp = client.post()
                .uri("/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(FileUploadResponse.class);

        if (resp == null || resp.fileId() == null) {
            throw new IllegalStateException("Storage returned empty response");
        }
        return resp.fileId();
    }
}
