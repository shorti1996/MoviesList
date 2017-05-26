package com.liebert.lab002.Models;

/**
 * Created by shorti1996 on 23.05.2017.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Credits extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cast")
    @Expose
    private RealmList<Cast> cast = null;
    @SerializedName("crew")
    @Expose
    private RealmList<Crew> crew = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RealmList<Cast> getCast() {
        return cast;
    }

    public void setCast(RealmList<Cast> cast) {
        this.cast = cast;
    }

    public RealmList<Crew> getCrew() {
        return crew;
    }

    public void setCrew(RealmList<Crew> crew) {
        this.crew = crew;
    }

}