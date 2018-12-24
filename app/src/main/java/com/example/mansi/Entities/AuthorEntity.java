package com.example.mansi.Entities;

public class AuthorEntity {

    private int _authorId;
    private String _authorName;
    private String _authorImageFile;

    public void setAuthorId(int authorId){ _authorId = authorId; }
    public int getAuthorId() { return _authorId; }

    public void setAuthorName(String authorName) { _authorName = authorName; }
    public String getAuthorName() { return _authorName; }

    public void setAuthorImageFile(String authorImageFile) { _authorImageFile = authorImageFile; }
    public String getAuthorImageFile() { return _authorImageFile; }

}
