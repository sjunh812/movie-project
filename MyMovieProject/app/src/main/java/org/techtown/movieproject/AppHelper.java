package org.techtown.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Movie;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;

import org.techtown.movieproject.api.CommentInfo;
import org.techtown.movieproject.api.CommentList;
import org.techtown.movieproject.api.MovieInfo;
import org.techtown.movieproject.api.MovieList;

import java.util.ArrayList;

public class AppHelper {
    public static RequestQueue requestQueue;
    private static SQLiteDatabase db;

    // 상수
    public static String host = "boostcourse-appapi.connect.or.kr";
    public static int port = 10000;
    public static String LOG = "AppHelper";

    // SQL 문
    private static String createMovieListSQL = "CREATE TABLE IF NOT EXISTS movielist " + "("        // 키 값(영화 id)
            + "_id integer PRIMARY KEY, "
            + "title text, "
            + "title_eng text, "
            + "date_value text, "
            + "user_rating float DEFAULT 0, "
            + "audience_rating float DEFAULT 0, "
            + "reviewer_rating float DEFAULT 0, "
            + "reservation_rate float DEFAULT 0, "
            + "reservation_grade integer DEFAULT 0, "
            + "grade integer DEFAULT 0, "
            + "thumb text, "
            + "image text);";
    private static String createMovieDetailsSQL = "CREATE TABLE IF NOT EXISTS moviedetails" + "("   // 키 값(영화 id)
            + "_id integer PRIMARY KEY, "
            + "title text, "
            + "date_value text, "
            + "user_rating float DEFAULT 0, "
            + "audience_rating float DEFAULT 0, "
            + "reviewer_rating float DEFAULT 0, "
            + "reservation_rate float DEFAULT 0, "
            + "reservation_grade integer DEFAULT 0, "
            + "grade integer DEFAULT 0, "
            + "thumb text, "
            + "image text, "
            + "photos text, "
            + "videos text, "
            + "outlinks text, "
            + "genre text, "
            + "duration integer DEFAULT 0, "
            + "audience integer DEFAULT 0, "
            + "synopsis text, "
            + "director text, "
            + "actor text, "
            + "_like integer DEFAULT 0, "
            + "_dislike integer DEFAULT 0, "
            + "totalcount integer DEFAULT 0);";
    private static String insertMovieListSQL = "INSERT OR REPLACE INTO movielist " + "values(?,?,?,?,?,?,?,?,?,?,?,?);";
    private static String insertMovieDetailsSQL = "INSERT OR REPLACE INTO moviedetails " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
    private static String selectMovielistSQL = "SELECT * FROM movielist;";
    private static String selectMovieDetailsSQL = "SELECT * FROM moviedetails;";

    public void openDB(Context context, String dbName) {
        DatabaseHelper helper = new DatabaseHelper(context, dbName, null, 1);       // version = 1
        db = helper.getWritableDatabase();
    }

    public void createTable(String tableName) {
        if(db != null) {
            if(tableName.equals("movielist")) {
                db.execSQL(createMovieListSQL);

                Log.d(LOG, "createTable() : 영화목록 테이블 생성됨.");
            } else if(tableName.equals("moviedetails")) {
                db.execSQL(createMovieDetailsSQL);

                Log.d(LOG, "createTable() : 영화상세보기 테이블 생성됨.");
            } else {        // 테이블 이름 : commentlist + 영화 id (ex) commentlist2)
                String createCommentListSQL = "CREATE TABLE IF NOT EXISTS " + tableName + "("
                        + "_id integer PRIMARY KEY, "
                        + "writer text, "
                        + "movieId integer, "
                        + "writer_image text, "
                        + "time_value text, "
                        + "timestamp_value integer, "
                        + "rating float, "
                        + "contents text, "
                        + "recommend integer);";
                db.execSQL(createCommentListSQL);

                Log.d(LOG, "createTable() : 한줄평목록 " + tableName + " 테이블 생성됨.");
            }
        } else {
            Log.d(LOG, "데이터베이스부터 오픈하세요..");
        }
    }

    public void insertMovieList(String tableName, MovieList movieList) {
        if(db != null) {
            if(tableName.equals("movielist")) {
                ArrayList<MovieInfo> list = movieList.result;

                for(int i = 0; i < list.size(); i++) {
                    MovieInfo info = list.get(i);
                    Object[] objs = {info.id, info.title, info.title_eng, info.date, info.user_rating, info.audience_rating, info.reviewer_rating,
                            info.reservation_rate, info.reservation_grade, info.grade, info.thumb, info.image};

                    db.execSQL(insertMovieListSQL, objs);
                }

                Log.d(LOG, "insertMovieList() : 영화목록 데이터 삽입됨.");
            } else if(tableName.equals("moviedetails")) {
                ArrayList<MovieInfo> list = movieList.result;

                for(int i = 0; i < list.size(); i++) {
                    MovieInfo info = list.get(i);
                    Object[] objs = {info.id, info.title, info.date, info.user_rating, info.audience_rating, info.reviewer_rating, info.reservation_rate,
                            info.reservation_grade, info.grade, info.thumb, info.image, info.photos, info.videos, info.outlinks, info.genre,
                            info.duration, info.audience, info.synopsis, info.director, info.actor, info.like, info.dislike, 0};        // 마지막 값 = 한줄평 총 개수(totalCount)

                    db.execSQL(insertMovieDetailsSQL, objs);
                }

                Log.d(LOG, "insertMovieList() : 영화상세보기 데이터 삽입됨.");
            }
        } else {
            Log.d(LOG, "데이터베이스부터 오픈하세요..");
        }
    }

    public void insertCommentList(String tableName, CommentList commentList) {
        if(db != null) {
            String insertCommentListSQL = "INSERT OR REPLACE INTO " + tableName + " values(?,?,?,?,?,?,?,?,?)";
            ArrayList<CommentInfo> list = commentList.result;

            for(int i = 0; i < list.size(); i++) {
                CommentInfo info = list.get(i);
                Object[] objs = {info.getId(), info.getWriter(), info.getMovieId(), info.getWriter_iamge(), info.getTime(), info.getTimestamp(),
                        info.getRating(), info.getContents(), info.getRecommend()};

                db.execSQL(insertCommentListSQL, objs);
            }

            Log.d(LOG, "insertCommentList() : 한줄평목록 데이터 삽입됨.");
        } else {
            Log.d(LOG, "데이터베이스부터 오픈하세요..");
        }
    }

    public void updateMovieCommentTotalCount(String tableName, int totalCount, int id) {
        if(db != null) {
            if(tableName.equals("moviedetails")) {
                String sql = "UPDATE moviedetails SET totalcount = " + totalCount + " WHERE _id = " + id;       // 해당 영화 id에 맞는 한줄평 총 개수 업데이트
                db.execSQL(sql);

                Log.d(LOG, "updateMovieCommentTotalCount() : 한줄평 총 개수 데이터 삽입됨. (영화 id = " + id + ")");
            }
        } else {
            Log.d(LOG, "데이터베이스부터 오픈하세요..");
        }
    }

    public MovieList selectMovieList(String tableName) {
        if(db != null) {
            if(tableName.equals("movielist")){
                Cursor cursor = db.rawQuery(selectMovielistSQL, null);
                ArrayList<MovieInfo> list = new ArrayList<>();

                while(cursor.moveToNext()) {
                    MovieInfo info = new MovieInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getFloat(4),
                            cursor.getFloat(5), cursor.getFloat(6), cursor.getFloat(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10),
                            cursor.getString(11));

                    list.add(info);
                }

                Log.d(LOG, "selectMovieList() : 영화목록 데이터 조회됨.");
                MovieList movieList = new MovieList();
                movieList.result = list;

                return movieList;
            }else if(tableName.equals("moviedetails")) {
                Cursor cursor = db.rawQuery(selectMovieDetailsSQL, null);
                ArrayList<MovieInfo> list = new ArrayList<>();

                while(cursor.moveToNext()) {
                    MovieInfo info = new MovieInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                            cursor.getFloat(4), cursor.getFloat(5), cursor.getFloat(6), cursor.getInt(7), cursor.getInt(8), cursor.getString(9),
                            cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getInt(15),
                            cursor.getInt(16), cursor.getString(17), cursor.getString(18), cursor.getString(19), cursor.getInt(20), cursor.getInt(21), cursor.getInt(22));

                    list.add(info);
                }

                Log.d(LOG, "selectMovieList() : 영화상세보기 데이터 조회됨.");
                MovieList movieList = new MovieList();
                movieList.result = list;

                return movieList;
            }
        }else {
            Log.d(LOG, "데이터베이스부터 오픈하세요..");
        }

        return null;
    }

    public CommentList selectCommentList(String tableName) {
        if(db != null) {
            String selectCommentListSQL = "SELECT * FROM " + tableName + " ORDER BY _id DESC;";
            try {
                Cursor cursor = db.rawQuery(selectCommentListSQL, null);
                ArrayList<CommentInfo> list = new ArrayList<>();

                while(cursor.moveToNext()) {
                    CommentInfo info = new CommentInfo(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3),
                            cursor.getString(4), cursor.getInt(5), cursor.getFloat(6), cursor.getString(7), cursor.getInt(8));

                    list.add(info);
                }

                Log.d(LOG, "selectCommentList() : 한줄평 데이터 조회됨.");
                CommentList commentList = new CommentList();
                commentList.result = list;

                return commentList;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }else {
            Log.d(LOG, "데이터베이스부터 오픈하세요..");
        }

        return null;
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG, "onCreate() : 데이터베이스 생성됨.");
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            Log.d(LOG, "onOpen() : 데이터베이스 오픈됨.");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(LOG, "onUpgrade() : 데이터베이스 갱신됨.");
        }
    }
}
