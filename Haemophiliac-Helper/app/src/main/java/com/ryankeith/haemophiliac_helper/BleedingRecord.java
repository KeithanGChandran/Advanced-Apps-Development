package com.ryankeith.haemophiliac_helper;

/**
 * Created by Jiechun on 20/04/2017.
 */
public class BleedingRecord {
    private int ID;
    private String date;
    private String part;
    private int condition;
    private String description;

    public BleedingRecord(String date, String part, int condition, String description) {
        super();
        this.date = date;
        this.part = part;
        this.condition = condition;
        this.description = description;
    }

    public int getID(){return ID;}

    public String getData(String data) {
        switch (data) {
            case "date":
                return date;
            case "part":
                return part;
            case "condition":
                return String.valueOf(condition);
            case "description":
                return description;
        }
        return "";
    }

    public void setID(int ID){this.ID=ID;}

    public void setData(String dataType, String data){
        switch (dataType){
            case "date":
                this.date = data;
            case "part":
                this.part = data;
            case "description":
                this.description = data;
        }
    }
    public void setData(String dataType, int data){
        this.condition = data;
    }

}
