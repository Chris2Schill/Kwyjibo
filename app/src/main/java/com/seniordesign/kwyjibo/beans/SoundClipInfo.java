package com.seniordesign.kwyjibo.beans;

// TODO: implement Comparable so we can sort by earliest to latest datetime.
public class SoundClipInfo{
    private int _id;
    private String _name;
    private String _createdBy;
    private String _location;
    private String _category;
    private String _filepath;
    private String _uploadDate;

    public SoundClipInfo() {
    }

    public SoundClipInfo(int id, String name, String createdBy, String location, String category,
                         String filepath, String uploadDate) {
        _id = id;
        _name = name;
        _createdBy = createdBy;
        _location = location;
        _category = category;
        _filepath = filepath;
        _uploadDate = uploadDate;
    }


    public String getCreatedBy() {
        return _createdBy;
    }
    public void setCreatedBy(String _createdBy) {
        this._createdBy = _createdBy;
    }

    public String getFilepath() {
        return _filepath;
    }
    public void setFilepath(String _filepath) {
        this._filepath = _filepath;
    }

    public int getId() {
        return _id;
    }
    public void setId(int _id) {
        this._id = _id;
    }

    public String getLocation() {
        return _location;
    }
    public void setLocation(String _location) {
        this._location = _location;
    }

    public String getCategory() {
        return _category;
    }
    public void setCategory(String _category) {
        this._category = _category;
    }

    public String getName() {
        return _name;
    }
    public void setName(String _name) {
        this._name = _name;
    }

    public String getUploadDate() {
        return _uploadDate;
    }
    public void setUploadDate(String _uploadDate) {
        this._uploadDate = _uploadDate;
    }

    public String toString(){
        return "{\"Id\":\"" + _id + "\","
             + " \"Name\":\"" + _name + "\","
             + " \"CreatedBy\":\"" + _createdBy + "\","
             + " \"Location\":\"" + _location + "\","
             + " \"Category\":\"" + _category + "\","
             + " \"Filepath\":\"" + _filepath + "\","
             + " \"UploadDate\":\"" + _uploadDate + "\"}";
    }
}
