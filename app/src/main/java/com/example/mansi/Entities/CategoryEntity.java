package com.example.mansi.Entities;


public class CategoryEntity {

    private int _categoryId;
    private String _categoryName;

    public void setCategoryId(int categoryId){
        _categoryId = categoryId;
    }
    public int getCategoryId(){
        return _categoryId;
    }

    public void setCategoryName(String categoryName){ _categoryName = categoryName; }
    public String getCategoryName(){
        return _categoryName;
    }

}
