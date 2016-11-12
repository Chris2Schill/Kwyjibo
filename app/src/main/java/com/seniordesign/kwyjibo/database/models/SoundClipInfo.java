package com.seniordesign.kwyjibo.database.models;

// TODO: implement Comparable so we can sort by earliest to latest datetime.
public class SoundClipInfo{
    public int Id;
    public String Name;
    public int CreatedById;
    public String CreatedByName;
    public String Location;
    public String Category;
    public String Filepath;
    public String UploadDate;
    public String Error;

    public SoundClipInfo() {
    }

    public SoundClipInfo(int id, String name, int createdById, String createdByName, String location, String category,
                        String filepath, String uploadDate) {
        Id = id;
        Name = name;
        CreatedById = createdById;
        CreatedByName = createdByName;
        Location = location;
        Category = category;
        Filepath = filepath;
        UploadDate = uploadDate;
    }

    public String toString(){
        return "{\"Id\":\"" + Id + "\","
             + " \"Name\":\"" + Name + "\","
             + " \"CreatedById\":\"" + CreatedById + "\","
             + " \"CreatedByName\":\"" + CreatedByName + "\","
             + " \"Location\":\"" + Location + "\","
             + " \"Category\":\"" + Category + "\","
             + " \"Filepath\":\"" + Filepath + "\","
             + " \"UploadDate\":\"" + UploadDate + "\","
                + " \"Error\":\"" + Error + "\"}";
    }
}
