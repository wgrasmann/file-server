package pl.wgrasmann.fileserver.factory;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import pl.wgrasmann.fileserver.model.FileMetadata;

@Service
public class HttpHeadersFactory {
    public HttpHeaders createFromFileMetadata(FileMetadata fileMetadata) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-Name", fileMetadata.getOriginalFileName());
        headers.add("File-Size", fileMetadata.getSize().toString());
        headers.add("File-Extension", fileMetadata.getExtension());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileMetadata.getOriginalFileName() + "\"");

        return headers;
    }
}
