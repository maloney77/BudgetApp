package com.example.budgetapp.ViewModels;

import androidx.lifecycle.ViewModel;

import com.example.budgetapp.POJO.BudgetCategory;

import java.math.BigDecimal;

public class CategoryEditViewModel extends ViewModel {
    private BudgetCategory budgetCategory;
    private Number categoryValue;
    private String cellToEdit;

    public void setCellToEdit(String cellToEdit) {
        this.cellToEdit = cellToEdit;
    }

    public String getCellToEdit() {
        return cellToEdit;
    }

    public Number getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(Number categoryValue) {
        this.categoryValue = categoryValue;
    }

    public void setBudgetCategory(BudgetCategory budgetCategory) {
        this.budgetCategory = budgetCategory;
    }

    public BudgetCategory getBudgetCategory() {
        return budgetCategory;
    }
}
