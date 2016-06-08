package com.seniordesign.kwyjibo.beans;

/**
 * Created by Chris on 6/6/16.
 */
public class RadioStation {
    public int Id;
    public String Name;
    public String CreatedBy;
    public String Genre;
    public int NumCurrentClips;

    public String toString(){
        return "{\"Id\":"+ Id
             + ",\"Name\":"+ Name
             + ",\"CreatedBy\":"+CreatedBy
             + ",\"Genre\":"+Genre
             + ",\"NumCurrentClips\":"+NumCurrentClips+"}";

    }


}
