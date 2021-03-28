package org.techtown.movieproject.api;

import java.io.Serializable;

public class CommentInfo implements Serializable {
    private int id;
    private String writer;
    private int movieId;
    private String writer_iamge;
    private String time;
    private long timestamp;
    private float rating;
    private String contents;
    private int recommend;

    public CommentInfo(int id, String writer, int movieId, String writer_iamge, String time, long timestamp, float rating, String contents, int recommend) {
        this.id = id;
        this.writer = writer;
        this.movieId = movieId;
        this.writer_iamge = writer_iamge;
        this.time = time;
        this.timestamp = timestamp;
        this.rating = rating;
        this.contents = contents;
        this.recommend = recommend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getWriter_iamge() {
        return writer_iamge;
    }

    public void setWriter_iamge(String writer_iamge) {
        this.writer_iamge = writer_iamge;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }
}
