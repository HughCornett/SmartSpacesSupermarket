package com.example.smartspacesblindshopping;

import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
    private boolean fakeItem;





    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Item() {
        //public no-arg constructor needed for firebase DB
    }

    //Custom constructor to serialise DB data into objects - Josh
    public Item(String Name, DocumentReference Brand, DocumentReference Category, String nfcTag, int aisle, int shelf, int row, int section, int level) {
        Log.d("item constructor", "running normal item constructor");
        this.productName = Name;
        this.productBrand = Brand;
        this.productCategory = Category;
        this.nfcTag = nfcTag;
        this.aisle = aisle;
        this.shelf = shelf;
        this.row = row;
        this.section = section;
        //this.brandName = " ";
        //this.categoryName = " ";
        this.level = level;
    }

    //Custom constructor to serialise DB data into objects - Josh
    public Item(String nfcTag, int aisle, int shelf, int section, int level) {
        Log.d("item constructor", "running null item constructor");
        this.productName = "blank";
        this.productBrand = null;
        this.productCategory = null;
        this.nfcTag = nfcTag;
        this.aisle = aisle;
        this.shelf = shelf;
        this.row = 0;
        this.section = section;
        this.brandName = " ";
        this.categoryName = " ";
        this.level = level;
    }

    public Item(String id, String name, int shelf, int level, int section)
    {
        this.id = id;
        this.productName = name;
        this.shelf = shelf;
        this.level = level;
        this.section = section;
    }


    public String getFullName(){
        return this.getBrandName() + " " + getProductName();
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


    public int getAisle() {
        return this.aisle;
    }


    @NonNull
    @Override
    public String toString() {
        return this.getProductName() + " " + this.getCategoryName() + " " + this.getBrandName() + " " + this.getNfcTag() + " " + this.getSection() + " " + this.getLevel() + " " + this.getAisle() + " " + this.getShelf();
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

    public boolean isFakeItem() {
        return fakeItem;
    }

    public void setFakeItem(boolean fakeItem) {
        this.fakeItem = fakeItem;
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

class Store {

    public ArrayList<StoreShelf> shelves = new ArrayList<>();


    public Store(ArrayList<Item> items) {
        //Log.d("Creating store", "Creating new store");
        //Log.d("items to add", "store items size is "+ items.size());
        int aisle = 0;

        for (int i = 0; i <= 3; i++) {
            ArrayList<Item> shelfItems = new ArrayList<Item>();
            for (Item item : items) {
                aisle = item.getAisle();
                if (item.getShelf() == i) {
                    //Log.d("Shelf List", "Adding item " + item.getProductName() + " with shelf number " + item.getShelf() +" to shelf " + i );
                    if (!shelfItems.contains(item)) {
                        shelfItems.add(item);
                    }
                }

            }
            if (!shelfItems.isEmpty()) {
                StoreShelf shelf = new StoreShelf(i, aisle, shelfItems);
                shelves.add(shelf);
            }
        }
        printStore();
    }

    public void printStore() {
        for (StoreShelf s : shelves) {
            s.printShelf();
        }
        //testProximity();
    }


}

class StoreShelf {

    public List<ArrayList<Item>> itemsOnShelf = new ArrayList<ArrayList<Item>>(2);
    public int shelfNo;

    public int aisle;

    public StoreShelf(int shelfNo, int aisle, ArrayList<Item> itemsToAdd) {
        initArray();
        fillShelf(itemsToAdd);
        this.shelfNo = shelfNo;
        this.aisle = aisle;
    }

    public List<ArrayList<Item>> getItems() {
        return itemsOnShelf;
    }

    public void setItems(ArrayList<ArrayList<Item>> items) {
        this.itemsOnShelf = items;
    }

    public void initArray() {
        for (int i = 0; i < itemsOnShelf.size(); i++) {
            itemsOnShelf.add(new ArrayList<Item>());
        }
    }

    public void printShelf() {
        Log.d("SHELF " + shelfNo, "PRINTING SHELF " + shelfNo);

        for (int i = 0; i < itemsOnShelf.size(); i++) {
            int inner = itemsOnShelf.get(i).size();
            Log.d("level " + i, "printing level " + i);

            for (int j = 0; j < inner; j++) {
                Log.d("section " + j, "item is " + itemsOnShelf.get(i).get(j).getProductName());

            }
        }
    }

    public void fillShelf(ArrayList<Item> itemsToAdd) {

        for (int i = 0; i < 2; i++) {
            //outer level
            ArrayList<Item> innerList = new ArrayList<Item>();
            for (int j = 0; j < 3; j++) {
                for (Item toAdd : itemsToAdd) {
                    if (toAdd.getLevel() == i && toAdd.getSection() == j) {
                        // Log.d("Inner List", "Adding item " + toAdd.getProductName() +" at level " + toAdd.getLevel() + " and section " + toAdd.getSection() );
                        innerList.add(toAdd);
                    }
                }
            }
            itemsOnShelf.add(innerList);
        }
    }



    public int getAisle() {
        return aisle;
    }

    public void setAisle(int aisle) {
        this.aisle = aisle;
    }


}
