package com.example.smartspacesblindshopping;

import android.graphics.Point;

import com.google.firebase.firestore.DocumentReference;

public class Item
{
    private int id;
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
    private Point position;

    public Item(){
        //public no-arg constructor needed for firebase DB
    }

    //for testing, before shelf/section/aisle layout is sorted out
    public Item(Point position, int aisle)
    {
        this.position = position;
        this.aisle = aisle;
    }

    //Custom constructor to serialise DB data into objects - Josh
    public Item(String Name,DocumentReference Brand, DocumentReference Category, String nfcTag, int aisle, int shelf, int row){
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

    public Item(int id, String name, int shelf, int level, int section)
    {
        this.id = id;
        this.productName = name;
        this.shelf = shelf;
        this.level = level;
        this.section = section;
    }


    //GETTERS & SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.productName = name;
    }

    public void setBrand(DocumentReference brand) {
        this.productBrand = brand;
    }

    public void setCategory(DocumentReference category) {
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

    public void setPosition(Point position) {
        this.position = position;
    }
    public int getAisle()
    {
        return this.aisle;
    }
    public Point getPosition()
    {
        return this.position;
    }

    public int getId() {
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

    public int getRow(){
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
 class Category{
    private String category;

    public Category(){
    }

    public Category(String categoryName){
        this.category = categoryName;
    }

    public String getCategoryName() {
        return category;
    }
}

 class Brand{
    private String brand;

    public Brand(){
    }

    public Brand(String brandName){
        this.brand = brandName;
    }

    public String getBrandName() {
        return brand;
    }
}
