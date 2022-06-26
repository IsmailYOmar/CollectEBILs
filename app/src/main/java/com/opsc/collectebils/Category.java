package com.opsc.collectebils;

public class Category
{

    public String userID;
    public String categoryName;
    public int goalNumber;

    public Category()
    {
    }

    public Category(String userID, String categoryName, int goalNumber)
    {
        this.userID = userID;
        this.categoryName = categoryName;
        this.goalNumber = goalNumber;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setGoalNumber(int goalNumber) {
        this.goalNumber = goalNumber;
    }

    public String getUserID() {
        return userID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getGoalNumber() {
        return goalNumber;
    }
}
