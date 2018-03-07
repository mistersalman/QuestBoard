package com.qbteam.questboard;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sazmi on 2/7/2018.
 */

public class QBUser {
    String bio;
    String email;
    String name;
    String education;
    int age;
    List<String> posts;
    float numberOfRatings;
    float totalStars;
    List<Float> ratings;
    double latitude;
    double longitude;
    String address;

    QBUser()
    {
        bio = "";
        email = "";
        name = "";
        education = "";
        age = 0;
        posts = new ArrayList<>();
        numberOfRatings = 0;
        totalStars = 0;
        ratings = new ArrayList<>();
        latitude = 0.0;
        longitude = 0.0;
        address = "";
    }

    QBUser(String email)
    {
        this.email = email;
        bio = "";
        name = "";
        education = "";
        age = 0;
        posts = new ArrayList<>();
        numberOfRatings = 0;
        totalStars = 0;
        ratings = new ArrayList<>();
        latitude = 0.0;
        longitude = 0.0;
        address = "";
    }

    void setBio(String bio)
    {
        this.bio = bio;
    }

    void setName(String name)
    {
        this.name = name;
    }

    void setEducation(String education)
    {
        this.education = education;
    }

    void addPost(String postName){this.posts.add(postName);}

    void addRating(Float rating){
        this.numberOfRatings++;
        this.totalStars += rating;
        this.ratings.add(rating);
    }

    String getBio()
    {
        return bio;
    }

    String getName()
    {
        return name;
    }

    String getEducation()
    {
        return education;
    }

    String getEmail()
    {
        return email;
    }

    int getAge()
    {
        return age;
    }

    List<String> getPosts(){return posts;}

    float getNumberOfRatings() {return numberOfRatings; }

    float getTotalStars() {return totalStars; }

    List<Float> getRatings() {return ratings; }
}
