package com.example.mysqlitedbconnection.csv.sqlite.Models;

import java.util.ArrayList;

/**
 * Created by Abu Muhsin on 26/08/2018.
 */

public class ListAndType {
    private ArrayList list;
    private Object object;

    public ListAndType(ArrayList list, Object object) {
        this.list = list;
        this.object = object;
    }

    public ArrayList getList() {
        return list;
    }

    public void setList(ArrayList list) {
        this.list = list;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
