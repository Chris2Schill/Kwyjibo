package com.seniordesign.kwyjibo.beans;

// TODO: implement Comparable so we can sort by earliest to latest datetime.
public class SoundClipInfo{
    public int Id;
    public String Name;
    public String CreatedBy;
    public String Location;
    public String Category;
    public String Filepath;
    public String UploadDate;

    public SoundClipInfo() {
    }

    public SoundClipInfo(int id, String name, String createdBy, String location, String category,
                        String filepath, String uploadDate) {
        Id = id;
        Name = name;
        CreatedBy = createdBy;
        Location = location;
        Category = category;
        Filepath = filepath;
        UploadDate = uploadDate;
    }

    public String toString(){
        return "{\"Id\":\"" + Id + "\","
             + " \"Name\":\"" + Name + "\","
             + " \"CreatedBy\":\"" + CreatedBy + "\","
             + " \"Location\":\"" + Location + "\","
             + " \"Category\":\"" + Category + "\","
             + " \"Filepath\":\"" + Filepath + "\","
             + " \"UploadDate\":\"" + UploadDate + "\"}";
    }
}
