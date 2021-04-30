package com.example.budgetapp.POJO;

public class BudgetCategory {
    String name;
    String column;
    Integer row;
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BudgetCategory(String name, String column, Integer row) {
        this.name = name;
        this.column = column;
        this.row = row;
    }

    public BudgetCategory(String name, String column, Integer row, String type) {
        this.name = name;
        this.column = column;
        this.row = row;
        this.type = type;
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
