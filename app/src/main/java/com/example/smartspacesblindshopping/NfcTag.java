package com.example.smartspacesblindshopping;

//This class is for localisation via nfc tags
public class NfcTag {

    private Item item;
    private String nfcCode;
    private int aisle;
    private int shelf;
    private int section;

    public NfcTag(Item itemReference){
        this.item = itemReference;
        this.nfcCode = item.getNfcTag();
        this.aisle = item.getAisle();
        this.shelf = item.getShelf();
        this.section = item.getSection();
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getNfcCode() {
        return nfcCode;
    }

    public void setNfcCode(String nfcCode) {
        this.nfcCode = nfcCode;
    }

    public int getAisle() {
        return aisle;
    }

    public void setAisle(int aisle) {
        this.aisle = aisle;
    }

    public int getShelf() {
        return shelf;
    }

    public void setShelf(int shelf) {
        this.shelf = shelf;
    }




}
