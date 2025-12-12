package getcoffee.antiplagiat.storage.store;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class FileStore {
    private final Path dir;

    public FileStore(@Value("${storage.dir}") String dir) throws Exception {
        this.dir = Path.of(dir);
        Files.createDirectories(this.dir);
    }

    public String save(byte[] bytes) throws Exception {
        String id = UUID.randomUUID().toString();
        Files.write(dir.resolve(id), bytes);
        return id;
    }

    public byte[] read(String id) throws Exception {
        return Files.readAllBytes(dir.resolve(id));
    }
}
