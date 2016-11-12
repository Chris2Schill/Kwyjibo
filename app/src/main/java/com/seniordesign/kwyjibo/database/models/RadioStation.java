package com.seniordesign.kwyjibo.database.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "stations")
public class RadioStation implements Serializable{

    private static final long serialVersionUID = -222864131214757025L;
    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String CREATED_BY = "CreatedBy";
    public static final String GENRE = "Genre";
    public static final String NUM_CURRENT_CLIPS = "NumCurrentClips";
    public static final double LENGTH = 0.0;
    public static String BPM = "BeatsPerMin";


    @DatabaseField(columnName = ID, id = true)
    public int Id;

    @DatabaseField(columnName = NAME, canBeNull = false)
    public String Name;

    @DatabaseField(columnName = CREATED_BY, canBeNull = false)
    public String CreatedBy;

    @DatabaseField(columnName = GENRE, canBeNull = false)
    public String Genre;


    @DatabaseField(columnName = NUM_CURRENT_CLIPS, canBeNull = false)
    public int NumCurrentClips;

//    @DatabaseField(columnName = BPM, canBeNull = false)
//    public String BPM;
//
//    @DatabaseField(columnName = LENGTH, canBeNull = false)
//    public double LENGTH;

    public String toString(){
        return "{\"Id\":"+ Id
             + ",\"Name\":"+ Name
             + ",\"CreatedBy\":"+CreatedBy
             + ",\"Genre\":"+Genre
                + ",\"BPM\":"+BPM
             + ",\"NumCurrentClips\":"+NumCurrentClips+"}";
    }
}
