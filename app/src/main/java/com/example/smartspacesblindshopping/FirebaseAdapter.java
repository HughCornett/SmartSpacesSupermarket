package com.example.smartspacesblindshopping;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FirebaseAdapter {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference productsRef = db.collection("Products");
    private CollectionReference brandsRef = db.collection("Brands");
    private CollectionReference categoriesRef = db.collection("Categories");

    private HashMap<String, Item> productsMap;
    private HashMap<String, Category> categoryMap;
    private HashMap<String, Brand> brandMap;

    static private ArrayList<Item> products = new ArrayList<>();

    public FirebaseAdapter() {
        productsMap = new HashMap<>();
        categoryMap = new HashMap<>();
        brandMap = new HashMap<>();
    }

    public void open(){
        db.enableNetwork();
    }

    public void close(){

        db.disableNetwork();
        db.terminate();
    }

    public ArrayList<Item> getItems(){
        return products;
    }

    //ONLY USE ONCE
    public void insertDummyData() {
//
//        //BRANDS
////        HashMap<String, Object> brand1 = new HashMap<>();
////        brand1.put("brand", "Nescafe");
////        DocumentReference nescafe = brands.add(brand1).getResult();
//
////        HashMap<String, Object> brand2 = new HashMap<>();
////        brand2.put("brand", "Boudin Bakery");
////        DocumentReference Boudin = brands.add(brand2).getResult();
//
//        HashMap<String, Object> b3 = new HashMap<>();
//        b3.put("brand", "Oatly");
//        DocumentReference Oatly = brands.add(b3).getResult();
//
//        HashMap<String, Object> b4 = new HashMap<>();
//        b4.put("brand", "Mars");
//        DocumentReference Mars = brands.add(b4).getResult();
//
//        HashMap<String, Object> b5 = new HashMap<>();
//        b5.put("brand", "Lindth");
//        DocumentReference Lindth = brands.add(b5).getResult();
//
//        HashMap<String, Object> b6 = new HashMap<>();
//        b6.put("brand", "Happy Eggs");
//        DocumentReference HappyEggs = brands.add(b6).getResult();
//
//        Log.d("Brands written", "brands written to DB");
//
//        //CATEGORIES
//
////        HashMap<String, Object> c1 = new HashMap<>();
////        c1.put("category", "Coffee");
////        DocumentReference coffee = brands.add(c1).getResult();
//
//        HashMap<String, Object> c2 = new HashMap<>();
//        c2.put("category", "Bread");
//        DocumentReference bread = brands.add(c2).getResult();
//
//        HashMap<String, Object> c3 = new HashMap<>();
//        c3.put("category", "Milk");
//        DocumentReference milk = brands.add(c3).getResult();
//
//        HashMap<String, Object> c4 = new HashMap<>();
//        c4.put("category", "Chocolate");
//        DocumentReference chocolate = brands.add(c4).getResult();
//
//        HashMap<String, Object> c5 = new HashMap<>();
//        c5.put("category", "Eggs");
//        DocumentReference eggs = brands.add(c5).getResult();
//
//        Log.d("Categories written", "cats written to DB");
//
//        //PRODUCTS
////        HashMap<String, Object> data1 = new HashMap<>();
////        data1.put("productName", "Black Coffee 500g");
////        data1.put("productBrand", nescafe);
////        data1.put("productCategory", coffee);
////        data1.put("nfcTag", "A930FDDE");
////        data1.put("aisle", 1);
////        data1.put("shelf",  2);
////        data1.put("row",   1);
////        products.add(data1);
//
////        HashMap<String, Object> data2 = new HashMap<>();
////        data2.put("productName", "Black Coffee 150g");
////        data2.put("productBrand", nescafe);
////        data2.put("productCategory", coffee);
////        data2.put("nfcTag", "A934A973");
////        data2.put("aisle", 1);
////        data2.put("shelf",  2);
////        data2.put("row",   1);
////        products.add(data2);
//
//        HashMap<String, Object> data3 = new HashMap<>();
//        data3.put("productName", "Mars Chocolate Bar");
//        data3.put("productBrand", Mars);
//        data3.put("productCategory", chocolate);
//        data3.put("nfcTag", "A9463F5E");
//        data3.put("aisle", 1);
//        data3.put("shelf",  2);
//        data3.put("row",   2);
//        products.add(data3);
//
//        HashMap<String, Object> data4 = new HashMap<>();
//        data4.put("productName", "Lindth 70% Chocolate Bar");
//        data4.put("productBrand", Lindth);
//        data4.put("productCategory", chocolate);
//        data4.put("nfcTag", "A934F67E");
//        data4.put("aisle", 1);
//        data4.put("shelf",  2);
//        data4.put("row",   2);
//        products.add(data4);
//
//        HashMap<String, Object> data5 = new HashMap<>();
//        data5.put("productName", "Chocolate Milk");
//        data5.put("productBrand", Oatly);
//        data5.put("productCategory", milk);
//        data5.put("nfcTag", "A933679E");
//        data5.put("aisle", 1);
//        data5.put("shelf",  2);
//        data5.put("row",   2);
//        products.add(data5);
//
//        HashMap<String, Object> data6 = new HashMap<>();
//        data6.put("productName", "Oat Milk");
//        data6.put("productBrand", Oatly);
//        data6.put("productCategory", milk);
//        data6.put("nfcTag", "A932FF5E");
//        data6.put("aisle", 1);
//        data6.put("shelf",  2);
//        data6.put("row",   1);
//        products.add(data6);
//
//        HashMap<String, Object> data7 = new HashMap<>();
//        data7.put("productName", "Eggs - Carton of 6");
//        data7.put("productBrand", HappyEggs);
//        data7.put("productCategory", eggs);
//        data7.put("nfcTag", "A941F0EE");
//        data7.put("aisle", 2);
//        data7.put("shelf",  4);
//        data7.put("row",   1);
//        products.add(data7);
//
////        HashMap<String, Object> data8 = new HashMap<>();
////        data8.put("productName", "Full Grain Bread - 1 Loaf");
////        data8.put("productBrand", Boudin);
////        data8.put("productCategory", bread);
////        data8.put("nfcTag", "A942CCEE");
////        data8.put("aisle", 2);
////        data8.put("shelf",  4);
////        data8.put("row",   1);
////        products.add(data8);
////
////        HashMap<String, Object> data9 = new HashMap<>();
////        data9.put("productName", "Chocolate Bun");
////        data9.put("productBrand", Boudin);
////        data9.put("productCategory", bread);
////        data9.put("nfcTag", "A94492CE");
////        data9.put("aisle", 2);
////        data9.put("shelf",  4);
////        data9.put("row",   1);
////        products.add(data9);
//
//

    }

    public void loadAllData() {
        loadBrands();
        loadCategories();

        //Delay to ensure categories and brands are loaded first
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getData(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Item> list) {
                Log.d("on callback", list.toString());
            }
        });

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listAllProducts();

    }

    public void listAllProducts() {
        if (products.size() == 0) {
                Log.d("array is empty", "products array is empty");
        }else{
            for (Item i : products) {
                Log.d("List all Products", i.getProductName() + ", Brand: " + i.getBrandName() + " AND Category: " + i.getCategoryName());
            }
        }

    }

    /**
     * Loads all products upon app start up, saves them to products array
     */
    public void loadAllProducts() {

    }

    private interface FirestoreCallback{
        void onCallback(ArrayList<Item> list);

    }

    private void getData(final FirestoreCallback firestoreCallback){
        products.clear();
        productsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                List<Item> data = task.getResult().toObjects(Item.class);
                                for (Item i : data) {
                                    i.setBrandName(getBrandNameByRef(i.getProductBrand()));
                                    i.setCategoryName(getCategoryNameByRef(i.getProductCategory()));
                                    products.add(i);
                                }
                                firestoreCallback.onCallback(products);
                            }
                        }else{
                            Log.d("DB Loadallproducts", "task unsuccesfull");
                        }
                    }
                });
    }

    /**
     * Returns the category name
     *
     * @param ref A document reference to the category
     * @return The category name
     */
    public String getCategoryNameByRef(DocumentReference ref) {
        for (HashMap.Entry<String, Category> entry : categoryMap.entrySet()) {
            if (ref.getId().equals(entry.getKey())) {

                return entry.getValue().getCategory();
            }
        }
        return null;
    }

    /**
     * Returns the brand name
     *
     * @param ref a document reference to the brand
     * @return the brand name
     */
    public String getBrandNameByRef(DocumentReference ref) {
        for (HashMap.Entry<String, Brand> entry : brandMap.entrySet()) {
            if (ref.getId().equals(entry.getKey().toString())) {
                return entry.getValue().getBrand();
            }
        }
        return null;
    }

    /**
     * Loads all categories upon app start up, saves them to categoriesMap
     */
    public void loadCategories() {
        categoriesRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Category c = document.toObject(Category.class);

                            categoryMap.put(document.getId(), c);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Load Category failed", "Failed to load Category");
                    }
                });
    }

    /**
     * Loads all brands upon app start up, saves them to brandsMaps
     */
    public void loadBrands() {
        brandsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Brand b = document.toObject(Brand.class);
                            brandMap.put(document.getId(), b);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Load brands failed", "Failed to load brands");
                    }
                });
    }

    /**
     * Returns a list of products by specified brand
     *
     * @param brandName a document reference to the brand
     * @return List of products
     */
    public ArrayList<Item> getItemsByBrand(String brandName) {
        ArrayList<Item> items = new ArrayList<>();

        for (Item i : products) {
            if (i.getBrandName().compareToIgnoreCase(brandName) == 0)  {
                items.add(i);
            }
        }
        return items;
    }


    /**
     * Returns a list of products by specified category
     *
     * @param categoryName a document reference to the category
     * @return List of products
     */
    public ArrayList<Item> getItemsByCategory(String categoryName) {
        ArrayList<Item> items = new ArrayList<>();
        Log.d("itemsbycategory", " products size is " + products.size());
        for (Item i : products) {
            if (i.getCategoryName().compareToIgnoreCase(categoryName) == 0) {
                items.add(i);
            }
        }
        return items;
    }

    /**
     * Returns a  products by specified NFC Tag
     *
     * @param NFC NFC tag to match
     * @return The item or null if item does not exist in the database
     */
    public Item getItemByNFCTag(String NFC) {
        for (Item i : products) {
            if (i.getNfcTag().equals(NFC)) {
                return i;
            }
        }
        return null;
    }
}
