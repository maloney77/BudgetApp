package com.example.budgetapp.POJO;

public enum BudgetMonth {
    JANUARY("January", "C"),
    FEBRUARY("February", "D"),
    MARCH("March", "E"),
    APRIL("April", "F"),
    MAY("May", "G"),
    JUNE("June", "H"),
    JULY("July", "I"),
    AUGUST("August", "J"),
    SEPTEMBER("September", "K"),
    OCTOBER("October", "L"),
    NOVEMBER("November", "M"),
    DECEMBER("December", "N");

    private String monthName;
    private String monthCellLetter;

    BudgetMonth(String monthName, String monthCellLetter) {
        this.monthCellLetter = monthCellLetter;
        this.monthName = monthName;
    }


    public String getMonthCellLetter() {
        return monthCellLetter;
    }
    public String getMonthCellLetter(String monthName) {
        return monthCellLetter;
    }

    public String getMonthName() {
        return monthName;
    }
}
