package pl.wgrasmann.fileserver.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.wgrasmann.fileserver.exception.MyFileNotFoundException;
import pl.wgrasmann.fileserver.properties.FileStorageProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileService {

    private final Path fileStorageLocation;

    public FileService(FileStorageProperties fileStorageProperties) throws FileUploadException {
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
        try {
            // Check if the file's name contains invalid characters
//            if(fileName.contains("..")) {
//                throw new FileUploadException("Sorry! Filename contains invalid path sequence " + fileName);
//            }

            // Copy file to the target location, replace existing with the same name
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {
            throw new FileUploadException("Could not upload file " + fileName, ex);
        }
    }

    public Resource downloadFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not fount " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not fount " + fileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                FileSystemUtils.deleteRecursively(filePath);
            } else {
                throw new MyFileNotFoundException("File not fount " + fileName);
            }
        } catch (MyFileNotFoundException | IOException ex) {
            throw new MyFileNotFoundException("Could not delete file " + fileName, ex);
        }
    }
}
