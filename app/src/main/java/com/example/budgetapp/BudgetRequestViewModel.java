package com.example.budgetapp;

import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;

import org.json.JSONArray;

public class BudgetRequestViewModel extends ViewModel {
    private RequestQueue requestQueue;

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
