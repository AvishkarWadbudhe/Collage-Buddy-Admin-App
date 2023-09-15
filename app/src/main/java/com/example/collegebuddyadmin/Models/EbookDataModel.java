package com.example.collegebuddyadmin.Models;

public class EbookDataModel {

    private String ebookTitle;
    private String ebookThumbnail;
    private String date;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    private String pdfUrl;

    public String getEbookTitle() {
        return ebookTitle;
    }

    public void setEbookTitle(String ebookTitle) {
        this.ebookTitle = ebookTitle;
    }

    public String getEbookThumbnail() {
        return ebookThumbnail;
    }

    public void setEbookThumbnail(String ebookThumbnail) {
        this.ebookThumbnail = ebookThumbnail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public EbookDataModel() {
    }

    public EbookDataModel(String ebookTitle, String ebookThumbnail, String date,String pdfUrl,String time,String key) {
        this.ebookTitle = ebookTitle;
        this.ebookThumbnail = ebookThumbnail;
        this.date = date;
        this.pdfUrl=pdfUrl;
        this.time =time;
        this.key=key;
    }
}
