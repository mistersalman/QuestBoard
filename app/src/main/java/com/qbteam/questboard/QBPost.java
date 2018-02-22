package com.qbteam.questboard;

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

    QBPost(String title, String description, String requirements, String rewards, List<String> tags)
    {
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.rewards = rewards;
        this.tags = tags;
    }

    String getTitle() {return title;}
    String getDescription() {return description;}
    String getRequirements() {return requirements;}
    String getRewards() {return rewards;}


}
