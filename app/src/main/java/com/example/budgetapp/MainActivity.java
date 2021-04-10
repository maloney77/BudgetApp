package com.example.budgetapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.toolbox.Volley;
import com.example.budgetapp.Fragments.FirstFragment;
import com.example.budgetapp.ViewModels.BudgetRequestViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private BudgetRequestViewModel requestViewModel;
    private static final String APPLICATION_NAME = "BudgetApp";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
    final String spreadsheetId = "1MGCUqJkQ5J-5KiZ_gdDU04AU2zZ9XH4eLPgZsTi4NjQ";
    final String range = "A1:E";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set requestViewModel
        requestViewModel = new ViewModelProvider(this).get(BudgetRequestViewModel.class);

        requestViewModel.setRequestQueue(Volley.newRequestQueue(this));

//        //set google sign in objects
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SheetsScopes.SPREADSHEETS))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //set google sign in button click event
        findViewById(R.id.sign_in_button).setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();

            startActivityForResult(signInIntent, 1);
        });


//        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
//        credential.setSelectedAccountName(settings.getString("kyle.maloney200@gmail.com", null));



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, SCOPES);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            requestViewModel.setTask(task);
            credential.setSelectedAccountName(task.getResult().getAccount().name);

            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();



            AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            ValueRange response = service.spreadsheets().values()
                                    .get(spreadsheetId, range)
                                    .execute();
                            Logger.getLogger("blah");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


//            List<List<Object>> values = new //response.getValues();
//            if (values == null || values.isEmpty()) {
//                System.out.println("No data found.");
//            } else {
//                System.out.println("Name, Major");
//                for (List row : values) {
//                    // Print columns A and E, which correspond to indices 0 and 4.
//                    System.out.printf("%s, %s\n", row.get(0), row.get(4));
//                }
//            }
            Logger.getLogger("test");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}