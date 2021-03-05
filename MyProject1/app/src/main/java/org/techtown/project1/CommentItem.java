package org.techtown.project1;

import java.io.Serializable;

public class CommentItem implements Serializable {
    String id;
    String comment;
    float rating;

    public CommentItem(String id, String comment, float rating) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
