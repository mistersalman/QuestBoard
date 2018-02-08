package com.qbteam.questboard;

/**
 * Created by sazmi on 2/7/2018.
 */

public class QBUser {
    String bio;
    String email;
    String name;
    String education;
    int age;

    QBUser()
    {
        bio = "";
        email = "";
        name = "";
        education = "";
        age = 0;
    }

    QBUser(String email)
    {
        this.email = email;
        bio = "";
        name = "";
        education = "";
        age = 0;
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
}
