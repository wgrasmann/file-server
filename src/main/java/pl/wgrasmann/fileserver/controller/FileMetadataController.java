package pl.wgrasmann.fileserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wgrasmann.fileserver.model.FileMetadata;
import pl.wgrasmann.fileserver.service.FileMetadataService;

import java.util.List;

@RestController
@RequestMapping("/file-metadata")
public class FileMetadataController {

    private final FileMetadataService fileMetadataService;

    @Autowired
    public FileMetadataController(FileMetadataService fileMetadataService) {
        this.fileMetadataService = fileMetadataService;
    }

    @GetMapping("/")
    public ResponseEntity<List<FileMetadata>> getAllMetadata() {
        List<FileMetadata> users = fileMetadataService.findAllMetaData();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<FileMetadata> getMetadata(@PathVariable("id") Long id) {
        FileMetadata fileMetadata = fileMetadataService.findFileMetadataByFileId(id);
        return new ResponseEntity<>(fileMetadata, HttpStatus.OK);
    }

    @GetMapping("/findbyname")
    public ResponseEntity<FileMetadata> getMetadataByFileName(@RequestParam String name) {
        FileMetadata fileMetadata = fileMetadataService.findFileMetadataByOriginalFileName(name);
        return new ResponseEntity<>(fileMetadata, HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity<FileMetadata> getMetadataByFileNameAndExtension(@RequestParam String name, @RequestParam String extension) {
        FileMetadata fileMetadata = fileMetadataService.findFileMetadataByOriginalFileNameOrExtension(name, extension);
        return new ResponseEntity<>(fileMetadata, HttpStatus.OK);
    }
}
