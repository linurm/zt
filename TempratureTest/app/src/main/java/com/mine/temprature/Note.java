package com.mine.temprature;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by Administrator on 2017/9/6.
 */
@Entity(indexes = {
        @Index(value = "temprature, date DESC", unique = true)
})
public class Note {
    @Id
    private Long id;

    @NotNull
    private String temprature;
    private String comment;
    private java.util.Date date;

    @Generated(hash = 148065447)
    public Note(Long id, @NotNull String temprature, String comment, java.util.Date date) {
        this.id = id;
        this.temprature = temprature;
        this.comment = comment;
        this.date = date;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }

    @NotNull
    public String getTemp() {
        return temprature;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setTemp(@NotNull String text) {
        this.temprature = text;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemprature() {
        return this.temprature;
    }

    public void setTemprature(String temprature) {
        this.temprature = temprature;
    }
}
