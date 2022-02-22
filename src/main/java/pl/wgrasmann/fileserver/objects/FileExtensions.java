package pl.wgrasmann.fileserver.objects;

public enum FileExtensions {
    TXT("txt"),
    PDF("pdf"),
    PNG("png");

    public final String value;

    private FileExtensions(String value) {
        this.value = value;
    }
}
