package com.example.budgetapp.Fragments;

import android.app.Service;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class FirstFragment extends Fragment {

    private BudgetRequestViewModel requestViewModel;
    private static final String APPLICATION_NAME = "BudgetApp";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

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

        //set request details
        String url = "https://jsonplaceholder.typicode.com/users";

        //get queue from view model
        requestViewModel = new ViewModelProvider(requireActivity()).get(BudgetRequestViewModel.class);
        RequestQueue requestQueue = requestViewModel.getRequestQueue();

        //enter price button listener
        view.findViewById(R.id.enter_price).setOnClickListener(v -> {
                System.out.println("got click event");
                //get categories
            try {
//                getSheetCategories();
            } catch (Exception e) {
                e.printStackTrace();
                Logger.getLogger("Failed to get sheet data");
            }
//            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                    response -> {

//                        System.out.println("got response");
//                        JSONArray categories = new JSONArray();
//                        // attempt to convert string to json array
//                        try {
//                            categories = new JSONArray(response);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        Logger.getLogger("Thing");
//                        //set categories for second fragment
//                        requestViewModel.setCategories(categories);
//                        NavHostFragment.findNavController(FirstFragment.this)
//                                .navigate(R.id.action_FirstFragment_to_SecondFragment);

//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Logger.getLogger("That didn't work!");
//                }
//            });

//            requestQueue.add(stringRequest);
        });


    }

//    private void createSheet(BudgetRequestViewModel budgetRequestViewModel) throws IOException {
//
//        Spreadsheet spreadsheet = new Spreadsheet()
//                .setProperties(new SpreadsheetProperties()
//                        .setTitle("test sheet"));
////        Credential credential = new Credential()
//
//        spreadsheet = service.spreadsheets().create(spreadsheet)
//                .setFields("spreadsheetId")
//                .execute();
//        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
//    }
//    private List<?> getSheetCategories() throws GeneralSecurityException, IOException {
//        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
//        final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//        final String range = "Class Data!A2:E";
//
//
//
//        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//        ValueRange response = service.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//        List<List<Object>> values = response.getValues();
//        if (values == null || values.isEmpty()) {
//            System.out.println("No data found.");
//        } else {
//            System.out.println("Name, Major");
//            for (List row : values) {
//                // Print columns A and E, which correspond to indices 0 and 4.
//                System.out.printf("%s, %s\n", row.get(0), row.get(4));
//            }
//        }
//        return values;
//    }

//    private  Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
//        // Load client secrets.
//        InputStream in = FirstFragment.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//
//        if (in == null) {
//            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//        }
//
//
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // Build flow and trigger user authorization request.
////        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
////                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
////                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
////                .setAccessType("offline")
////                .build();
//
//
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//
//
//    }





}