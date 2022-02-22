package pl.wgrasmann.fileserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.wgrasmann.fileserver.exception.MyFileNotFoundException;
import pl.wgrasmann.fileserver.model.FileMetadata;
import pl.wgrasmann.fileserver.repository.FileMetadataRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;

    @Autowired
    public FileMetadataService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileMetadata addFileMetadata(FileMetadata fileMetadata) {
        return fileMetadataRepository.save(fileMetadata);
    }

    public FileMetadata findFileMetadataByFileId(Long id) {
        FileMetadata fileMetadata = fileMetadataRepository.findFileMetadataById(id).orElse(null);
        if (fileMetadata == null) {
            throw new MyFileNotFoundException("File not fount " + id);
        }
        return fileMetadata;
    }

    public List<FileMetadata> findAllMetaData() {
        return fileMetadataRepository.findAll();
    }

    public FileMetadata findFileMetadataByOriginalFileName(String fileName) {
        return fileMetadataRepository.findFileMetadataByOriginalFileName(fileName).orElse(null);
    }

    public FileMetadata findFileMetadataByOriginalFileNameOrExtension(String name, String extension) {
        return fileMetadataRepository.findFileMetadataByOriginalFileNameOrExtension(name, extension).orElse(null);
    }

    public void delete(FileMetadata fileMetadata) {
        fileMetadataRepository.delete(fileMetadata);
    }

    public void save(FileMetadata fileMetadata) {
        fileMetadataRepository.save(fileMetadata);
    }
}
