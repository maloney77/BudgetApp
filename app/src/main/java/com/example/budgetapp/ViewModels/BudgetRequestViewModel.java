package com.example.budgetapp.ViewModels;

import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.api.services.sheets.v4.Sheets;

import org.json.JSONArray;

import java.util.ArrayList;

public class BudgetRequestViewModel extends ViewModel {
    //request queue that is passed from activities to fragments
    private RequestQueue requestQueue;

    //list of budget categories passed between fragments
    private ArrayList categories;

    private Sheets sheetService;

    public Sheets getSheetService() {
        return sheetService;
    }
    public void setSheetService(Sheets inputSheetService) {
        sheetService = inputSheetService;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
    public void setRequestQueue(RequestQueue inputQueue) {
        requestQueue = inputQueue;
    }

    public ArrayList getCategories() {
        return categories;
    }
    public void setCategories(ArrayList inputCategories) {
        categories = inputCategories;
    }
}
