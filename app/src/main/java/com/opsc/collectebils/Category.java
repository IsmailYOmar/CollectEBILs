package com.opsc.collectebils;

public class Category {
    public String userID;

    public String getCategoryName() {
        return categoryName;
    }

    public String categoryName;
    public String goalNumber;

    public Category() {

    }

    public Category(String userID, String categoryName, String goalNumber) {
        this.userID = userID;
        this.categoryName = categoryName;
        this.goalNumber = goalNumber;
    }
    public String toString(){return this. categoryName;}
    public String getGoalNumber(){return this.categoryName + " Goal to reach: " + this.goalNumber;}
}
