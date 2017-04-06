package com.ryankeith.haemophiliac_helper;

/**
 * Created by RenruiLiu on 6/04/2017.
 */
public class InfusionRecord {
    private int ID;
    private String date;
    private String dose;
    private String type;
    private String description;

    public InfusionRecord(String date, String dose, String type, String description) {
        super();
        this.date = date;
        this.dose = dose;
        this.type = type;
        this.description = description;
    }

    public int getID(){return ID;}
    public String getData(String data) {
        switch (data) {
            case "date":
                return date;
            case "dose":
                return dose;
            case "type":
                return type;
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
            case "dose":
                this.dose = data;
            case "type":
                this.type = data;
            case "description":
                this.description = data;
        }
    }
}
