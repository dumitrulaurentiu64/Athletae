package com.example.athletae;



import java.util.Date;

public class Post extends PostId {

    public String user_id,  image_url, desc, image_thumb, post_city;
    public Date timestamp;

    public Post(String user_id, String image_url, String desc, String image_thumb, String post_city, Date timestamp) {
        this.user_id = user_id;
        this.post_city = post_city;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public Post(){}
    public String getPost_city() {
        return post_city;
    }

    public void setPost_city(String post_city) {
        this.post_city = post_city;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
