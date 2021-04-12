package com.example.budgetapp.POJO;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetSheet {

    String spreadSheetId;
    ArrayList<BudgetCategory> categories;

    public String getSpreadSheetId() {
        return spreadSheetId;
    }

    public void setSpreadSheetId(String spreadSheetId) {
        this.spreadSheetId = spreadSheetId;
    }

    public BudgetSheet(ArrayList<BudgetCategory> categories, String spreadSheetId) {
        this.categories = categories;
        this.spreadSheetId = spreadSheetId;
    }

    public BudgetSheet() {
        this.categories = new ArrayList<>();
    }

    public void addCategory(BudgetCategory budgetCategory) {
        this.categories.add(budgetCategory);
    }

    public ArrayList<BudgetCategory> getCategories() {
        return categories;
    }
    public void setCategories(ArrayList<BudgetCategory> categories) {
        this.categories = categories;
    }
}
