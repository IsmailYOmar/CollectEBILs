package com.opsc.collectebils;

public class Category
{
    public String userID;
    public String categoryName;
    public String goalNumber;

    public Category()
    {
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public Category(String userID, String categoryName, String goalNumber)
    {
        this.userID = userID;
        this.categoryName = categoryName;
        this.goalNumber = goalNumber;
    }
    public String toString()
    {
        return this. categoryName;
    }
    public String returnGoalNumber()
    {
        return this.categoryName + ": " + this.goalNumber+" ";
    }
}
