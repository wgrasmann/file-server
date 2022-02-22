package pl.wgrasmann.fileserver.factory;

import org.springframework.stereotype.Service;
import pl.wgrasmann.fileserver.model.FileMetadata;

@Service
public class FileMetadataFactory {
    public FileMetadata create(String name, String extension, Long size) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setOriginalFileName(name);
        fileMetadata.setExtension(extension);
        fileMetadata.setSize(size);

        return fileMetadata;
    }
}
