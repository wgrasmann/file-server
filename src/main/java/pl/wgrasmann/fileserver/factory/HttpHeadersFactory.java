package pl.wgrasmann.fileserver.factory;

import org.springframework.stereotype.Service;
import pl.wgrasmann.fileserver.model.FileMetadata;

@Service
public class FileMetadataFactory {
    public FileMetadata createFileMetadata(String fileName, Long size, String extension) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setOriginalFileName(fileName);
        fileMetadata.setSize(size);
        fileMetadata.setExtension(extension);

        return fileMetadata;
    }
}
