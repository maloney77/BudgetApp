package com.example.budgetapp.ViewModels;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;
import com.example.budgetapp.POJO.BudgetSheet;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class BudgetRequestViewModel extends ViewModel {
    //request queue that is passed from activities to fragments
    private RequestQueue requestQueue;

    //list of budget categories passed between fragments
    private BudgetSheet budgetSheet;

    private Sheets sheetService;

    private boolean signedIn;

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public ValueRange getValuesFromSheet(String range) {
        final ValueRange[] response = {null};

        AsyncTask.execute(() -> {
            try{
                response[0] = sheetService.spreadsheets().values()
                        .get(budgetSheet.getSpreadSheetId(), range)
                        .execute();

            } catch (IOException e) {
                e.printStackTrace();
                Logger.getLogger("Failed to get sheet data");
            }
        });
        return response[0];
    }

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

    public BudgetSheet getBudgetSheet() {
        return budgetSheet;
    }
    public void setBudgetSheet(BudgetSheet budgetSheet) {
        this.budgetSheet = budgetSheet;
    }
}
