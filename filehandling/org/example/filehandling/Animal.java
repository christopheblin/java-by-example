package org.example.filehandling;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Map;

public abstract class Animal implements Comparable<Animal> {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private String name;
    private String color;
    private Date birthday;
    private float weight;

    public Animal(Map<String,String> dataRow) {
        if(dataRow.containsKey("name")) {
            this.setName(dataRow.get("name"));
        }
        if(dataRow.containsKey("birthday")) {
            this.setBirthday(dataRow.get("birthday"));
        }
        if(dataRow.containsKey("color")) {
            this.setColor(dataRow.get("color"));
        }
        if(dataRow.containsKey("weight")) {
            this.setWeight(Float.parseFloat(dataRow.get("weight")));
        }
    }

    public Animal(String name, String birthday, String color, float weight) {
        this.setName(name);
        this.setBirthday(birthday);
        this.setColor(color);
        this.setWeight(weight);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public Date getBirthday() {
        return birthday;
    }
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
    public String getBirthdayAsString() {
        return sdf.format(birthday);
    }
    public void setBirthday(String birthday) {
        this.birthday = sdf.parse(birthday, new ParsePosition(0));
    }
    public float getWeight() {
        return weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int compareTo(Animal a) {
        return this.name.compareTo(a.name);
    }

    public void print() {
        System.out.println("Name: "+name);
        System.out.println("Color: "+color);
        System.out.println("Birthday: "+this.getBirthdayAsString());
        System.out.println("Weight: " +weight);
    }
}