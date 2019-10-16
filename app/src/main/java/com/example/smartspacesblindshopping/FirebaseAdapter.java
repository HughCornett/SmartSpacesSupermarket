package com.example.smartspacesblindshopping;


import android.util.Log;

import androidx.annotation.NonNull;

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

public class FirebaseAdapter {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference products = db.collection("Products");
    private CollectionReference brands = db.collection("Brands");
    private CollectionReference categories = db.collection("Categories");

    private HashMap<String, Item> productsMap = new HashMap<String, Item>();
    private HashMap<String, Category> categoryMap = new HashMap<String, Category>();
    private HashMap<String, Brand> brandMap = new HashMap<String, Brand>();


    public FirebaseAdapter() {

    }

    //ONLY USE ONCE
    public void loadDummyData() {
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

    public void init() {
        loadBrands();
        loadCategories();
        loadAllProducts();
    }

    public Boolean close() {

        return true;
    }

    /**
     * Loads all products upon app start up, saves them to productsMap
     */
    public void loadAllProducts() {
        products.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("Load Products success", document.getId() + " => " + document.getData());
                            Item i = document.toObject(Item.class);
                            i.setBrandName(getBrandNameByRef(i.getProductBrand()));
                            //Log.d("item Brand is ", i.getBrandName().toString());
                            productsMap.put(document.getId(), i);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Load Products failed", "Failed to load products");
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
        Iterator it = categoryMap.entrySet().iterator();

        if (it != null) {
            while (it.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) it.next();
                if (ref.getId().equals(entry.getKey().toString())) {
                    return entry.getValue().toString();
                }
            }
        } else {
            Log.d("iterator is empty ", "empty iterator");
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
        Iterator it = brandMap.entrySet().iterator();
        if (it != null) {
            while (it.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) it.next();
                Log.d("entry id is", entry.getKey().toString());
                Log.d("reference id is", ref.getId());
                if (ref.getId().equals(entry.getKey().toString())) {
                    return entry.getValue().toString();
                }
            }
        } else {
            Log.d("iterator is empty ", "empty iterator");
        }
        return null;
    }

    /**
     * Loads all categories upon app start up, saves them to categoriesMap
     */
    public void loadCategories() {
        categories.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("Load Category success", document.getId() + " => " + document.getData());
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
        brands.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("Load brands success", document.getId() + " => " + document.getData());
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
     * @param brandRef a document reference to the brand
     * @return List of products
     */
    public ArrayList<Item> getProductsByBrand(DocumentReference brandRef) {
        ArrayList<Item> items = new ArrayList<>();

        for (Item i : productsMap.values()) {
            if (i.getProductBrand().equals(brandRef)) {
                items.add(i);
            }
        }
        return items;
    }

    /**
     * Returns a list of products by specified category
     *
     * @param categoryRef a document reference to the category
     * @return List of products
     */
    public ArrayList<Item> getProductsByCategory(DocumentReference categoryRef) {
        ArrayList<Item> items = new ArrayList<>();

        for (Item i : productsMap.values()) {
            if (i.getProductBrand().equals(categoryRef)) {
                items.add(i);
            }
        }
        return items;
    }
}
