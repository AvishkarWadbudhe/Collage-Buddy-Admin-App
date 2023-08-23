package com.example.collagebuddyadmin.Models;

import java.io.Serializable;

public class NoticeDataModel implements Serializable {
    String title;

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    String notice;
    String image;
    String date;
    String time;
    String key;

    public NoticeDataModel() {
    }

    public NoticeDataModel(String title,String notice, String image, String date, String time, String key) {
        this.title = title;
        this.notice = notice;
        this.image = image;
        this.date = date;
        this.time = time;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String data) {
        this.date = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
