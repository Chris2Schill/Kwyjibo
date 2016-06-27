package com.seniordesign.kwyjibo.beans;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "stations")
public class RadioStation implements Serializable{

    private static final long serialVersionUID = -222864131214757024L;

    @DatabaseField(columnName = "Id", id = true)
    public int Id;

    @DatabaseField(columnName = "Name", canBeNull = false)
    public String Name;

    @DatabaseField(columnName = "CreatedBy", canBeNull = false)
    public String CreatedBy;

    @DatabaseField(columnName = "Genre", canBeNull = false)
    public String Genre;

    @DatabaseField(columnName = "NumCurrentClips", canBeNull = false)
    public int NumCurrentClips;

    public String toString(){
        return "{\"Id\":"+ Id
             + ",\"Name\":"+ Name
             + ",\"CreatedBy\":"+CreatedBy
             + ",\"Genre\":"+Genre
             + ",\"NumCurrentClips\":"+NumCurrentClips+"}";

    }


}
