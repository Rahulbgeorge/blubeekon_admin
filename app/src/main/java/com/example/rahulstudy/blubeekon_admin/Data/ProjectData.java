package com.example.rahulstudy.blubeekon_admin.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul study on 10-05-2018.
 */

public class ProjectData {

    public List<String> tags=new ArrayList<>();
    private String name;
    private String about;
    private int like;
    private int dislike;
    private String imageurl;
    private double latitude;
    private double longitude;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private long time;
    public String getBeacon() {
        return beacon;
    }

    public void setBeacon(String beacon) {
        this.beacon = beacon;
    }

    private String beacon;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }



    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


    public ProjectData()
    {name="";
    about="";
    like=0;
    dislike=0;}

    public ProjectData(String name, String about)
    {
        this.name=name;
        this.about=about;
        this.like=0;
        this.dislike=0;
        this.beacon="none";
        this.time=0;

    }


}
