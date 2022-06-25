package com.opsc.collectebils;

import androidx.annotation.Nullable;

public class Wishlist {

    public String userID;
    public String categoryName;
    public String categoryKey;
    public String itemName;
    public String itemDescription;
    @Nullable
    public String imgFileName;

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    public String getImgFileName()
    {
        return imgFileName;
    }

    public void setImgFileName(String imgFileName)
    {
        this.imgFileName = imgFileName;
    }

    public Wishlist(String userID, String categoryName, String categoryKey,String  itemName,
                 String  itemDescription , String imgFileName)
    {
        this.userID = userID;
        this.categoryName = categoryName;
        this.categoryKey = categoryKey;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.imgFileName = imgFileName;
    }
}
