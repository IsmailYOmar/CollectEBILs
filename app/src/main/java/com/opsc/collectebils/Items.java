package com.opsc.collectebils;

import androidx.annotation.Nullable;

public class Items {

    public String userID;

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

    public String getCategoryKey()
    {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey)
    {
        this.categoryKey = categoryKey;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }
    @Nullable
    public String getItemDescription()
    {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription)
    {
        this.itemDescription = itemDescription;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    public String getProductionYear()
    {
        return productionYear;
    }

    public void setProductionYear(String productionYear)
    {
        this.productionYear = productionYear;
    }

    public String getPurchasePrice()
    {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice)
    {
        this.purchasePrice = purchasePrice;
    }

    public String getPurchaseDate()
    {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate)
    {
        this.purchaseDate = purchaseDate;
    }

    public String getImgFileName()
    {
        return imgFileName;
    }

    public void setImgFileName(String imgFileName)
    {
        this.imgFileName = imgFileName;
    }

    public String categoryName;
    public String categoryKey;
    public String itemName;
    public String itemDescription;
    public String manufacturer;
    public String productionYear;
    public String purchasePrice;
    public String purchaseDate;
    @Nullable
    public String imgFileName;

    public Items()
    {

    }

    public Items(String userID, String categoryName, String categoryKey,String  itemName,
                 String  itemDescription ,String manufacturer, String productionYear,
                 String purchasePrice, String  purchaseDate, String imgFileName)
    {
            this.userID = userID;
            this.categoryName = categoryName;
            this.categoryKey = categoryKey;
            this.itemName = itemName;
            this.itemDescription = itemDescription;
            this.manufacturer = manufacturer;
            this.productionYear = productionYear;
            this.purchasePrice = purchasePrice;
            this.purchaseDate = purchaseDate;
            this.imgFileName = imgFileName;
        }
    }