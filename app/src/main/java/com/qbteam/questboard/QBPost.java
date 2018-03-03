package com.qbteam.questboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sazmi on 2/19/2018.
 */

public class QBPost {
    String title;
    String description;
    String requirements;
    String rewards;
    List<String> tags;
    List<String> applicantIDs;
    String posterID;
    String hiredID;
    boolean hired;
    boolean completed;
    boolean rated;

    QBPost()
    {
        title = "";
        description = "";
        requirements = "";
        rewards = "";
        tags = new ArrayList<String>();
        applicantIDs = new ArrayList<String>();
        posterID = "";
        hiredID = "";
        hired = false;
        completed = false;
        rated = false;
    }

    QBPost(String title, String description, String requirements, String rewards, List<String> tags, String posterID)
    {
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.rewards = rewards;
        this.tags = tags;
        applicantIDs = new ArrayList<String>();
        this.posterID = posterID;
        hiredID = "";
        hired = false;
        completed = false;
        rated = false;
    }

    String getTitle() {return title;}
    String getDescription() {return description;}
    String getRequirements() {return requirements;}
    String getRewards() {return rewards;}
    List<String> getTags(){return tags;}
    List<String> getApplicants() {return applicantIDs;}
    String getPosterID() {return posterID;}
    String hiredID() {return hiredID;}
    boolean getHired() {return hired;}
    boolean getCompleted() {return completed;}
    boolean getRated() {return rated;}

}
