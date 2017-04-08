package com.liebert.lab002.Models;

import io.realm.RealmObject;

/**
 * Created by shorti1996 on 08.04.2017.
 */

public class RealmInt extends RealmObject {
    public int val;

    public RealmInt() {
    }

    public RealmInt(int val) {
        this.val = val;
    }

    public int getInt() {
        return val;
    }

    public void setInt(int value) {
        this.val = value;
    }
}
