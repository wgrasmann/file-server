package pl.wgrasmann.fileserver.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.wgrasmann.fileserver.exception.FileExtensionNotSupportedException;
import pl.wgrasmann.fileserver.exception.MyFileNotFoundException;
import pl.wgrasmann.fileserver.factory.FileMetadataFactory;
import pl.wgrasmann.fileserver.model.FileMetadata;
import pl.wgrasmann.fileserver.objects.FileExtensions;
import pl.wgrasmann.fileserver.properties.FileStorageProperties;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;

@Service
@Transactional
public class FileService {

    private final Path fileStorageLocation;
    private final FileMetadataService fileMetadataService;
    private final FileMetadataFactory fileMetadataFactory;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties,
                       FileMetadataService fileMetadataService,
                       FileMetadataFactory fileMetadataFactory) throws FileUploadException {

        this.fileMetadataService = fileMetadataService;
        this.fileMetadataFactory = fileMetadataFactory;

        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileUploadException("Could not create file upload directory.", ex);
        }
    }

    public void uploadFile(MultipartFile file) throws FileUploadException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (Arrays.stream(FileExtensions.values()).noneMatch((t) -> t.name().equalsIgnoreCase(extension))) {
            throw new FileExtensionNotSupportedException("This file type is not yet supported: " + extension);
        }

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileMetadata fileMetadata = fileMetadataService.findFileMetadataByOriginalFileName(fileName);

            if (fileMetadata == null) {
                fileMetadata = fileMetadataFactory.create(file.getOriginalFilename(), fileName.substring(fileName.lastIndexOf(".") + 1), file.getSize());

                fileMetadataService.addFileMetadata(fileMetadata);
            } else {
                fileMetadata.setSize(file.getSize());
                fileMetadataService.save(fileMetadata);
            }
        } catch (IOException ex) {
            throw new FileUploadException("Could not upload file " + fileName, ex);
        }
    }

    public Resource getFileAsResource(String name) {

        Resource resource = findFile(name);

        if (!resource.exists()) {
            throw new MyFileNotFoundException("File not fount " + name);
        }

        return resource;
    }

    private Resource findFile(String originalFileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(originalFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not fount " + originalFileName);
            }

        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not fount " + originalFileName, ex);
        }
    }

    public void deleteFile(Long id) {
        try {

            FileMetadata fileMetadata = fileMetadataService.findFileMetadataByFileId(id);

            if (fileMetadata == null) {
                throw new MyFileNotFoundException("File not fount " + id);
            }

            Path filePath = this.fileStorageLocation.resolve(fileMetadata.getOriginalFileName()).normalize();
            FileSystemUtils.deleteRecursively(filePath);

            fileMetadataService.delete(fileMetadata);

        } catch (MyFileNotFoundException | IOException ex) {
            throw new MyFileNotFoundException("Could not delete file " + id, ex);
        }
    }
}
