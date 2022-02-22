package pl.wgrasmann.fileserver.model;

import javax.persistence.*;

@Entity
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String originalFileName;
    private String extension;
    private Long size;

    public FileMetadata() {
    }

    public FileMetadata(String originalFileName, String extension, Long size) {
        this.originalFileName = originalFileName;
        this.extension = extension;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

}
