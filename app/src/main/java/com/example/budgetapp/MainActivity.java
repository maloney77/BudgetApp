package com.example.budgetapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.toolbox.Volley;
import com.example.budgetapp.ViewModels.BudgetRequestViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

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
    final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();

    final int RC_SIGN_IN = 1;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //set requestViewModel
        requestViewModel = new ViewModelProvider(this).get(BudgetRequestViewModel.class);

        requestViewModel.setRequestQueue(Volley.newRequestQueue(this));

        //set google sign in client with permissions
        GoogleSignInClient mGoogleSignInClient = initializeGoogleSignInClient();

        //get last google account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        View signInButton = findViewById(R.id.sign_in_button);

       //TODO: hide sign in button after logging in and switching frames
        if(account == null) {
            //hide welcome message and enter price button
            signInButton.setVisibility(View.VISIBLE);
            requestViewModel.setSignedIn(false);

        } else {
            //hide sign in button and display front fragment
            requestViewModel.setSignedIn(true);
            signInButton.setVisibility(View.GONE);
            signInToSheetService(account);
        }

        //set google sign in button click event
        signInButton.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();

            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

    }

    private GoogleSignInClient initializeGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SheetsScopes.SPREADSHEETS))
                .requestEmail()
                .build();
        return  GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            signInToSheetService(task.getResult());

            Logger.getLogger("test");
        }
    }

    private void signInToSheetService(GoogleSignInAccount googleAccount) {
        // Authenticate to sheet service and store the service
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, SCOPES);
        credential.setSelectedAccountName(googleAccount.getAccount().name);

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        requestViewModel.setSheetService(service);

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