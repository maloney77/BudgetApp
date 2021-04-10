package com.example.budgetapp.POJO;

public class BudgetCategory {
    String name;
    String column;
    Integer row;

    public BudgetCategory(String name, String column, Integer row) {
        this.name = name;
        this.column = column;
        this.row = row;
    }

    public String getCell() {
        return this.column + this.row.toString();
    }

    public String getCategoryMonthCell(String month) {
        BudgetMonth budgetMonth = BudgetMonth.valueOf(month);
        return budgetMonth.getMonthCellLetter() + this.row.toString();
    }

    public String getName() {
        return name;
    }

    public Integer getRow() {
        return row;
    }

    public String getColumn() {
        return column;
    }
}
