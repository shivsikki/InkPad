package com.example.inkpad.memories;

public class Memory {
    private String description;
    private String mediaUri;
    private String mediaType;
    private byte[] imageBytes;

    public Memory(String description, String mediaUri, String mediaType, byte[] imageBytes) {
        this.description = description;
        this.mediaUri = mediaUri;
        this.mediaType = mediaType;
        this.imageBytes = imageBytes;
    }
    public String getDescription() {
        return description;
    }
    public String getMediaUri() {
        return mediaUri;
    }
    public String getMediaType() {
        return mediaType;
    }
    public byte[] getImageBytes() {
        return imageBytes;
    }
}
