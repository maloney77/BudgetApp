package com.example.budgetapp.POJO;

import com.example.budgetapp.ViewModels.BudgetRequestViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetSheet {

    String spreadSheetId;
    ArrayList<BudgetCategory> categories;
    BudgetCategory totalSpentForMonth;
    BudgetCategory totalRemainingForMonth;

    public BudgetCategory getTotalRemainingForMonth() {
        return totalRemainingForMonth;
    }

    public void setTotalRemainingForMonth(BudgetCategory totalRemainingForMonth) {
        this.totalRemainingForMonth = totalRemainingForMonth;
    }

    public BudgetCategory getTotalSpentForMonth() {
        return totalSpentForMonth;
    }

    public void setTotalSpentForMonth(BudgetCategory totalSpentForMonth) {
        this.totalSpentForMonth = totalSpentForMonth;
    }

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
