package org.techtown.movieproject.gallery;

public class GalleryInfo {
    private int id;
    private String url;
    private String thumbnailUrl;
    private boolean isYoutube;

    public GalleryInfo(int id, String url, String thumbnailUrl, boolean isYoutube) {
        this.id = id;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.isYoutube = isYoutube;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean isYoutube() {
        return isYoutube;
    }

    public void setYoutube(boolean youtube) {
        isYoutube = youtube;
    }
}
