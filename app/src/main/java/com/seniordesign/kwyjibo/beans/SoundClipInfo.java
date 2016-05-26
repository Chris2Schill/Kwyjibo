package com.seniordesign.kwyjibo.beans;

public class SoundClipInfo {
    private int _id;
    private String _soundclipname;
    private String _contributor;
    private String _location;

    public SoundClipInfo() {
    }

    public SoundClipInfo(int _id, String _soundclipname, String _contributor, String _location) {
        this._id = _id;
        this._soundclipname = _soundclipname;
        this._contributor = _contributor;
        this._location = _location;
    }

    public String getContributor() {
        return _contributor;
    }

    public void setContributor(String _contributor) {
        this._contributor = _contributor;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String _location) {
        this._location = _location;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getSoundClipName() {
        return _soundclipname;
    }

    public void setSoundClipName(String _soundclipname) {
        this._soundclipname = _soundclipname;
    }
}
