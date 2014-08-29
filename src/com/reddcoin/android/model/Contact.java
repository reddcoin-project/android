package com.reddcoin.android.model;

public class Contact {
    
    //private variables
    int _id;
    String _name;
    String _address;
    String _owner;
     
    // Empty constructor
    public Contact(){
         
    }
    // constructor
    public Contact(int id, String name, String address, String owner){
        this._id = id;
        this._name = name;
        this._address = address;
        this._owner = owner;
    }
     
    // constructor
    public Contact(String name, String _address){
        this._name = name;
        this._address = _address;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
     
    // setting id
    public void setID(int id){
        this._id = id;
    }
     
    // getting name
    public String getName(){
        return this._name;
    }
     
    // setting name
    public void setName(String name){
        this._name = name;
    }
     
    // getting address
    public String getAddress(){
        return this._address;
    }
     
    // setting address
    public void setAddress(String address){
        this._address = address;
    }
   
    // getting owner
    public String getOwner(){
        return this._owner;
    }
     
    // setting owner
    public void setOwner(String owner){
        this._owner = owner;
    }
    
}

