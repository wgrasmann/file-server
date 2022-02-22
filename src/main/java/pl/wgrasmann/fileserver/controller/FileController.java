package pl.wgrasmann.fileserver.controller;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.wgrasmann.fileserver.exception.MyFileNotFoundException;
import pl.wgrasmann.fileserver.factory.HttpHeadersFactory;
import pl.wgrasmann.fileserver.model.FileMetadata;
import pl.wgrasmann.fileserver.service.FileMetadataService;
import pl.wgrasmann.fileserver.service.FileService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final FileMetadataService fileMetadataService;
    private final HttpHeadersFactory httpHeadersFactory;

    @Autowired
    public FileController(FileService fileService, HttpHeadersFactory httpHeadersFactory, FileMetadataService fileMetadataService) {
        this.fileService = fileService;
        this.httpHeadersFactory = httpHeadersFactory;
        this.fileMetadataService = fileMetadataService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile file) {
        try {
            fileService.uploadFile(file);
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable("id") Long id) {

        try {
            FileMetadata fileMetadata = fileMetadataService.findFileMetadataByFileId(id);
            Resource resource = fileService.getFileAsResource(fileMetadata.getOriginalFileName());
            HttpHeaders responseHeaders = httpHeadersFactory.createFromFileMetadata(fileMetadata);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .headers(responseHeaders)
                    .body(resource);
        } catch (MyFileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable("id") Long id) {
        try {
            fileService.deleteFile(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok("File deleted successfully");
    }
}
