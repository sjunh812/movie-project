package org.techtown.movieproject.api;

import java.io.Serializable;

public class MovieInfo implements Serializable {
    public int id;
    public String title;
    public String title_eng;
    public String date;
    public float user_rating;
    public float audience_rating;
    public float reviewer_rating;
    public float reservation_rate;
    public int reservation_grade;
    public int grade;
    public String thumb;
    public String image;

    // 영화 상세보기
    public String photos;
    public String videos;
    public String outlinks;
    public String genre;
    public int duration;
    public int audience;
    public String synopsis;
    public String director;
    public String actor;

    // 좋아요 싫어요
    public int like;
    public int dislike;
}
