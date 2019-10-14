package com.example.smartspacesblindshopping;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FirebaseAdapter {

    public FirebaseAdapter(){
        loadBrands();
        loadCategories();
        loadProducts();

    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference products = db.collection("Products");
    private CollectionReference brands = db.collection("Brands");
    private CollectionReference categories = db.collection("Categories");

    private HashMap<String,Item> productsArray = new HashMap<String,Item>();
    private HashMap<String,Category> categoryArray = new HashMap<String, Category>();
    private HashMap<String,Brand>  brandArray = new HashMap<String, Brand>();


    public void loadDummyData(){

    }

    public Boolean open(){

        return true;
    }

    public Boolean close(){

        return true;
    }

    public void loadProducts(){
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
                            productsArray.put(document.getId(), i);
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

    public String getCategoryNameByRef(DocumentReference ref){
        Iterator it = categoryArray.entrySet().iterator();

        if(it != null){
            while (it.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) it.next();
                if(ref.getId().equals(entry.getKey().toString())){
                    return entry.getValue().toString();
                }

            }
        }else{
            Log.d("iterator is empty " , "empty iterator");
        }

        return null;
    }

    public String getBrandNameByRef(DocumentReference ref){
        Iterator it = brandArray.entrySet().iterator();

        if(it != null){


        while (it.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) it.next();
            Log.d("entry id is", entry.getKey().toString());
            Log.d("reference id is", ref.getId());
            if(ref.getId().equals(entry.getKey().toString())){
                return entry.getValue().toString();
            }
        }
        }else{
            Log.d("iterator is empty " , "empty iterator");
        }

        return null;
    }

    public void loadCategories(){
        categories.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("Load Category success", document.getId() + " => " + document.getData());
                            Category c = document.toObject(Category.class);
                            categoryArray.put(document.getId(), c);
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

    public void loadBrands(){
        brands.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("Load brands success", document.getId() + " => " + document.getData());
                            Brand b = document.toObject(Brand.class);
                            brandArray.put(document.getId(), b);
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
}
