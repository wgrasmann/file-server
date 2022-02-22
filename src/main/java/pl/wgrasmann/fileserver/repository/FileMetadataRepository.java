package pl.wgrasmann.fileserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wgrasmann.fileserver.model.FileMetadata;

import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    Optional<FileMetadata> findFileMetadataById(Long id);
    Optional<FileMetadata> findFileMetadataByOriginalFileName(String fileName);
    Optional<FileMetadata> findFileMetadataByExtension(String extension);
    Optional<FileMetadata> findFileMetadataByOriginalFileNameOrExtension(String fileNae, String extension);}
