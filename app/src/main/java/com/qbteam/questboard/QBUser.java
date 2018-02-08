package com.qbteam.questboard;

/**
 * Created by sazmi on 2/7/2018.
 */

public class QBUser {
    String bio;
    String email;
    String name;
    String education;
    String resume_filepath;
    String picture_filepath;
    int age;

    QBUser()
    {

    }

    QBUser(String email)
    {
        this.email = email;
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

    void setResume_filepath(String resume_filepath)
    {
        this.resume_filepath = resume_filepath;
    }

    void setPicture_filepath(String picture_filepath)
    {
        this.picture_filepath = picture_filepath;
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

    String getResume_filepath()
    {
        return resume_filepath;
    }

    String getPicture_filepath()
    {
        return picture_filepath;
    }
}
