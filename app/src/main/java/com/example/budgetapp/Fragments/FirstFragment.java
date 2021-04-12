package com.example.budgetapp.Fragments;

import android.app.Service;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.budgetapp.POJO.BudgetCategory;
import com.example.budgetapp.POJO.BudgetSheet;
import com.example.budgetapp.R;
import com.example.budgetapp.ViewModels.BudgetRequestViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class FirstFragment extends Fragment {

    private BudgetRequestViewModel requestViewModel;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */

    final String spreadsheetId = "1MGCUqJkQ5J-5KiZ_gdDU04AU2zZ9XH4eLPgZsTi4NjQ";
    final String range = "A18:A25";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get queue from view model
        requestViewModel = new ViewModelProvider(requireActivity()).get(BudgetRequestViewModel.class);

        View welcomeMessage = view.findViewById(R.id.textview_first);
        View enterPriceButton = view.findViewById(R.id.enter_price);

        //TODO: hide sign in button after logging in and figure out why the other two are null
        if(!requestViewModel.isSignedIn() ) {
            //hide welcome message and enter price button
            welcomeMessage.setVisibility(View.GONE);
            enterPriceButton.setVisibility(View.GONE);
        }


        //enter price button listener
        view.findViewById(R.id.enter_price).setOnClickListener(v -> {
                System.out.println("got click event");
                //get categories

            BudgetSheet budgetSheet = new BudgetSheet(new ArrayList<>(), spreadsheetId);
            requestViewModel.setBudgetSheet(budgetSheet);

            AsyncTask.execute(() -> {
                try{
                    ValueRange response = requestViewModel.getSheetService().spreadsheets().values()
                            .get(requestViewModel.getBudgetSheet().getSpreadSheetId(), range)
                            .execute();
                    if(response != null) {
                        //TODO: add failure to retrieve snackbar


                        for (int i = 0; i < response.getValues().size(); i++) {
                            String name = response.getValues().get(i).toString().replace("[","").replace("]","");
                            Integer row = Integer.parseInt(response.getRange().replace("Sheet1!", "").split(":")[0].substring(1)) + i;
                            String column = "" + response.getRange().replace("Sheet1!", "").split(":")[0].charAt(0);

                            budgetSheet.addCategory(new BudgetCategory(name, column, row));
                        }

                        requestViewModel.setBudgetSheet(budgetSheet);
                        Logger.getLogger("Retrieved categories");
                        //navigate to next fragment
                        NavHostFragment.findNavController(FirstFragment.this)
                                .navigate(R.id.action_FirstFragment_to_SecondFragment);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.getLogger("Failed to get sheet data");
                }
            });



        });


    }







}