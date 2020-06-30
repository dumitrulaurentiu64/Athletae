package com.example.athletae;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class PostId {
    @Exclude
    public String BlogPostId;

    public String getBlogPostId() {
        return BlogPostId;
    }

    public void setBlogPostId(String blogPostId) {
        BlogPostId = blogPostId;
    }

    public <T extends PostId> T withId(@NonNull final String id){
        this.BlogPostId = id;
        return (T) this;
    }
}
