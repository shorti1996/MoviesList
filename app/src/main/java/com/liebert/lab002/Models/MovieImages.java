package com.liebert.lab002.Models;

import java.util.LinkedList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MovieImages extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("backdrops")
    @Expose
    private RealmList<Backdrop> backdrops = null;

    @SerializedName("posters")
    @Expose
    private RealmList<Poster> posters = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RealmList<Backdrop> getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(RealmList<Backdrop> backdrops) {
        this.backdrops = backdrops;
    }

    public RealmList<Poster> getPosters() {
        return posters;
    }

    public void setPosters(RealmList<Poster> posters) {
        this.posters = posters;
    }

}
