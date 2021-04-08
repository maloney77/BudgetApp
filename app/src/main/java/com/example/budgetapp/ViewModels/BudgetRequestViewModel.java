package com.example.budgetapp.ViewModels;

import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;

import org.json.JSONArray;

public class BudgetRequestViewModel extends ViewModel {
    //request queue that is passed from activities to fragments
    private RequestQueue requestQueue;

    //list of budget categories passed between fragments
    private JSONArray categories;

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
    public void setRequestQueue(RequestQueue inputQueue) {
        requestQueue = inputQueue;
    }

    public JSONArray getCategories() {
        return categories;
    }
    public void setCategories(JSONArray inputCategories) {
        categories = inputCategories;
    }
}
