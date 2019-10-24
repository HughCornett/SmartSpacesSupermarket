package com.example.smartspacesblindshopping;

import android.graphics.Point;

import com.google.firebase.firestore.DocumentReference;

public class Item {
    private String id;
    private String productName;
    private DocumentReference productBrand;
    private String brandName;
    private DocumentReference productCategory;
    private String categoryName;
    private String nfcTag;
    private int row;
    private int aisle;
    private int shelf;
    private int section;
    private int level;
    private double xPosition;
    private double yPosition;

    public Item() {
        //public no-arg constructor needed for firebase DB
    }

    //Custom constructor to serialise DB data into objects - Josh
    public Item(String Name, DocumentReference Brand, DocumentReference Category, String nfcTag, int aisle, int shelf, int row) {
        this.productName = Name;
        this.productBrand = Brand;
        this.productCategory = Category;
        this.nfcTag = nfcTag;
        this.aisle = aisle;
        this.shelf = shelf;
        this.row = row;
        this.brandName = "";
        this.categoryName = "";
    }

    public Item(String id, String name, int shelf, int level, int section)
    {
        this.id = id;
        this.productName = name;
        this.shelf = shelf;
        this.level = level;
        this.section = section;

        //the x position is the middle of the shelf's rectangle
        Shelf thisShelf = Map.shelves.get(shelf);
        this.xPosition = (thisShelf.getRect().left + ((thisShelf.getRect().right - thisShelf.getRect().left)/2));
        //the y position is the position of the section on the shelf
        double sectionWidth = (thisShelf.getRect().bottom - thisShelf.getRect().top) / thisShelf.getNumberOfSections();
        this.yPosition = thisShelf.getRect().top + (section+0.5)*sectionWidth;
    }


    //GETTERS & SETTERS

    public void setId(String id) {
        this.id = id;
    }

    public void setProductName(String name) {
        this.productName = name;
    }

    public void setProductBrand(DocumentReference brand) {
        this.productBrand = brand;
    }

    public void setProductCategory(DocumentReference category) {
        this.productCategory = category;
    }

    public void setNfcTag(String nfcTag) {
        this.nfcTag = nfcTag;
    }

    public void setAisle(int aisle) {
        this.aisle = aisle;
    }

    public void setShelf(int shelf) {
        this.shelf = shelf;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public void setXPosition(double xPosition) {
        this.xPosition = xPosition;
    }
    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public int getAisle() {
        return this.aisle;
    }
    public double getXPosition()
    {
        return this.xPosition;
    }
    public double getYPosition()
    {
        return this.yPosition;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public DocumentReference getProductBrand() {
        return productBrand;
    }

    public DocumentReference getProductCategory() {
        return productCategory;
    }

    public String getNfcTag() {
        return nfcTag;
    }

    public int getShelf() {
        return shelf;
    }

    public int getSection() {
        return section;
    }

    public int getRow() {
        return row;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

//Smaller Classes for firebase serialisation
class Category {
    private String category;

    public Category() {
    }

    public Category(String categoryName) {
        this.category = categoryName;
    }

    public String getCategory() {

        return category;
    }

    public void setCategory(String c) {
        this.category = c;
    }
}

class Brand {
    private String brand;

    public Brand() {
    }

    public Brand(String brandName) {
        this.brand = brandName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String b) {
        this.brand = b;
    }
}
