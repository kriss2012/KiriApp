package com.kirigenplatform.data.model;

public class UserStats {
    private int connections;
    private int profileViews;
    private int posts;
    private int achievements;
    
    public UserStats() {}
    
    public int getConnections() {
        return connections;
    }
    
    public void setConnections(int connections) {
        this.connections = connections;
    }
    
    public int getProfileViews() {
        return profileViews;
    }
    
    public void setProfileViews(int profileViews) {
        this.profileViews = profileViews;
    }
    
    public int getPosts() {
        return posts;
    }
    
    public void setPosts(int posts) {
        this.posts = posts;
    }
    
    public int getAchievements() {
        return achievements;
    }
    
    public void setAchievements(int achievements) {
        this.achievements = achievements;
    }
}
