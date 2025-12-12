package getcoffee.antiplagiat.storage.api;

import getcoffee.antiplagiat.storage.store.FileStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FilesController {

    private final FileStore store;

    public FilesController(FileStore store) {
        this.store = store;
    }

    public record FileUploadResponse(String fileId) {}

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileUploadResponse upload(@RequestPart("file") MultipartFile file) throws Exception {
        String id = store.save(file.getBytes());
        return new FileUploadResponse(id);
    }

    @GetMapping(value = "/files/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] download(@PathVariable String id) throws Exception {
        return store.read(id);
    }
}
