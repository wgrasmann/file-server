package pl.wgrasmann.fileserver.controller;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.wgrasmann.fileserver.exception.MyFileNotFoundException;
import pl.wgrasmann.fileserver.service.FileMetadataService;
import pl.wgrasmann.fileserver.service.FileService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final FileMetadataService fileMetadataService;

    @Autowired
    public FileController(FileService fileService, FileMetadataService fileMetadataService) {
        this.fileService = fileService;
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

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable("fileName") String fileName, HttpServletRequest request) {

        try {
            Resource resource = fileService.downloadFile(fileName);

//            String contentType = null;
//            try {
//                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//            } catch (IOException ex) {
////                logger.info("Could not determine file type.");
//            }
//
//            // Fallback to the default content type if type could not be determined
//            if(contentType == null) {
//                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
//            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MyFileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @DeleteMapping("/delete/{fileName:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileName") String fileName) {
        try {
            fileService.deleteFile(fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok("File deleted successfully");
    }
}
