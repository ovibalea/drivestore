package com.project.ovi.liceenta.model;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Ovi on 23/08/16.
 */
public class ItemAttribute implements ParentListItem {

    private static final DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

    private String name;
    private List<String> values;


    public ItemAttribute(String name, String[] values) {
        this.name = name;
        this.values = Arrays.asList(values);
    }

    public ItemAttribute(String name, String value) {
        this.name = name;
        this.values = Arrays.asList(value);
    }

    public ItemAttribute(String name, Long value) {
        this.name = name;
        this.values = Arrays.asList(value.toString());
    }

    public ItemAttribute(String name, Boolean value) {
        this.name = name;
        this.values = Arrays.asList(value.toString());
    }

    public ItemAttribute(String name, DateTime dateValue) {
        this.name = name;
        Date date = new Date(dateValue.getValue());
        this.values = Arrays.asList(dateFormat.format(date));
    }

    public ItemAttribute(String name, User userValue) {
        this.name = name;
        this.values = Arrays.asList(userValue.getDisplayName() + " - " + userValue.getEmailAddress());
    }

    public ItemAttribute(String name, List<User> usersValue) {
        this.name = name;
        this.values = getUsersInfo(usersValue);
    }

    private List<String> getUsersInfo(List<User> users){
        List<String> usersInfo = new ArrayList<>();
        for(User user : users){
            usersInfo.add(user.getDisplayName() + " - " + user.getEmailAddress());
        }
        return usersInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void addValue(String value){
        values.add(value);
    }

    public String getValuesToString(){
        String valuesString = "";
        for(String value : values){
            valuesString = valuesString + ", " + value;
        }
        if(valuesString.length() > 2) {
            return valuesString.substring(2);
        }
        return "";
    }

    @Override
    public List<String> getChildItemList() {
        return values;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
