package com.example.collagebuddyadmin.Models;

public class FacultyDataModel {
    String name;
    String designation;
    String imageUrl;
    String contact;
    String uniqueKey;

    public FacultyDataModel() {
    }

    public FacultyDataModel(String name, String designation, String imageUrl, String contact, String uniqueKey) {
        this.name = name;
        this.designation = designation;
        this.imageUrl = imageUrl;
        this.contact = contact;
        this.uniqueKey = uniqueKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
