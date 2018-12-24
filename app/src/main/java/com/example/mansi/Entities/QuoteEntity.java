package com.example.mansi.Entities;

import android.os.Parcel;
import android.os.Parcelable;


public class QuoteEntity implements Parcelable {

    private int _quoteId;
    private String _quoteText;
    private int _authorId;
    private int _categoryId;
    private String _categoryName;
    private int _favorite;
    private String _authorName;
    private String _authorImageFile;

    public QuoteEntity() {
    }

    protected QuoteEntity(Parcel in) {
        _quoteId = in.readInt();
        _quoteText = in.readString();
        _authorId = in.readInt();
        _categoryId = in.readInt();
        _categoryName = in.readString();
        _favorite = in.readInt();
        _authorName = in.readString();
        _authorImageFile = in.readString();
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_quoteId);
        dest.writeString(_quoteText);
        dest.writeInt(_authorId);
        dest.writeInt(_categoryId);
        dest.writeString(_categoryName);
        dest.writeInt(_favorite);
        dest.writeString(_authorName);
        dest.writeString(_authorImageFile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuoteEntity> CREATOR = new Creator<QuoteEntity>() {
        @Override
        public QuoteEntity createFromParcel(Parcel in) {
            return new QuoteEntity(in);
        }

        @Override
        public QuoteEntity[] newArray(int size) {
            return new QuoteEntity[size];
        }
    };

    public int getQuoteId(){
        return _quoteId;
    }
    public void setQuoteId(int quoteId){
        _quoteId = quoteId;
    }

    public String getQuoteText(){
        return _quoteText;
    }
    public void setQuoteText(String quoteText){
        _quoteText = quoteText;
    }

    public int getAuthorId(){ return _authorId; }
    public void setAuthorId(int authorId){ _authorId = authorId; }

    public int getCategoryId(){
        return _categoryId;
    }
    public void setCategoryId(int categoryId){
        _categoryId = categoryId;
    }

    public String getCategoryName(){ return _categoryName; }
    public void setCategoryName(String categoryName){ _categoryName = categoryName; }




    public String getAuthorName(){ return _authorName; }
    public void setAuthorName(String authorName){ _authorName = authorName; }

    public String getAuthorImageFile(){ return _authorImageFile; }
    public void setAuthorImageFile(String authorImageFile){ _authorImageFile = authorImageFile; }


}
