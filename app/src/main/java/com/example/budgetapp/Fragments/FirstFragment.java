package com.example.budgetapp.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.budgetapp.POJO.BudgetCategory;
import com.example.budgetapp.POJO.BudgetSheet;
import com.example.budgetapp.R;
import com.example.budgetapp.ViewModels.BudgetRequestViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.json.JsonFactory;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class FirstFragment extends Fragment {

    private BudgetRequestViewModel requestViewModel;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */

    final String spreadsheetId = "1MGCUqJkQ5J-5KiZ_gdDU04AU2zZ9XH4eLPgZsTi4NjQ";
    final String range = "A1:A500";
    BudgetSheet budgetSheet = new BudgetSheet(new ArrayList<>(), spreadsheetId);

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

        View spentOverViewCard = view.findViewById(R.id.spent_card);
        View enterPriceButton = view.findViewById(R.id.enter_price);

        //TODO: hide sign in button after logging in and figure out why the other two are null
        if(!requestViewModel.isSignedIn() ) {
            //hide welcome message and enter price button
            spentOverViewCard.setVisibility(View.GONE);
            enterPriceButton.setVisibility(View.GONE);
        }


        requestViewModel.setBudgetSheet(budgetSheet);
        AsyncTask.execute(() -> {
            try{


                ValueRange spreadSheetResponse = requestViewModel.getSheetService().spreadsheets().values()
                        .get(requestViewModel.getBudgetSheet().getSpreadSheetId(), range)
                        .execute();
                if(spreadSheetResponse != null) {

                    //populate the budget sheet from the spreadsheet
                    populateBillCategoriesFromSpreadsheet(spreadSheetResponse);
                    populateOverViewCategoriesFromSpreadsheet(spreadSheetResponse);

                    requestViewModel.setBudgetSheet(budgetSheet);

                    ValueRange overViewValues = getOverviewValuesForCurrentMonth();
                    if(overViewValues != null) {
                        //populate overview cards
                        TextView spentTextView = view.findViewById(R.id.spent_amount);
                        spentTextView.setText(overViewValues.getValues().get(0).get(0).toString());

                        TextView remainingViewText = view.findViewById(R.id.remaining_amount);
                        remainingViewText.setText(overViewValues.getValues().get(1).get(0).toString());
                    }


                    Logger.getLogger("Retrieved categories");

                } else {
                    Snackbar.make(getView(), "Failed to retrieve spreadsheet data, try reloading the app" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


            } catch (IOException e) {
                Snackbar.make(getView(), "Failed to retrieve spreadsheet data, try reloading the app" , Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                e.printStackTrace();
                Logger.getLogger("Failed to get sheet data");
            }
        });



        //enter price button listener
        view.findViewById(R.id.enter_price).setOnClickListener(v -> {
                System.out.println("got click event");
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });


    }

    private ValueRange getOverviewValuesForCurrentMonth() throws IOException {
        String currentMonthSpentCell = budgetSheet.getTotalSpentForMonth().getCategoryMonthCell(LocalDate.now().getMonth().name());
        String currentMonthRemainingCell = budgetSheet.getTotalRemainingForMonth().getCategoryMonthCell(LocalDate.now().getMonth().name());

        return requestViewModel.getSheetService().spreadsheets().values()
                .get(requestViewModel.getBudgetSheet().getSpreadSheetId(), currentMonthSpentCell + ":" + currentMonthRemainingCell)
                .execute();

    }


    private void populateBillCategoriesFromSpreadsheet(ValueRange sheetValues) {
            //populate all the bill categories
            for (int i = 1; i < sheetValues.getValues().size(); i++) {
                if (sheetValues.getValues().get(i).isEmpty()) {
                    //skip the empty cells
                } else if (sheetValues.getValues().get(i).get(0).toString().toLowerCase().contains("categories")) {
                    //skip the cell that says categories
                } else if (sheetValues.getValues().get(i).toString().toLowerCase().contains("total")) {
                //stop storing categories when you reach the total cell and set which row this is at for the next loop
                    break;

                }else {
                    //set the category name, row and column.
                    String name = sheetValues.getValues().get(i).get(0).toString();
                    Integer row = i + 1;
                    String column = "A";
                    String type = "bills";

                    budgetSheet.addCategory(new BudgetCategory(name, column, row, type));
                }

            }

    }

    private void populateOverViewCategoriesFromSpreadsheet(ValueRange sheetValues) {
        for (int i = 1; i < sheetValues.getValues().size(); i++) {
            if (sheetValues.getValues().get(i).toString().toLowerCase().contains("spent")){
                //get the how much you spent section

                String name = sheetValues.getValues().get(i).get(0).toString();
                Integer row = i + 1;
                String column = "A";
                String type = "aggregate";

                budgetSheet.setTotalSpentForMonth(new BudgetCategory(name, column, row, type));

            } else if (sheetValues.getValues().get(i).toString().toLowerCase().contains("have left")){
                //get the how much you have left section
                String name = sheetValues.getValues().get(i).get(0).toString();
                Integer row = i + 1;
                String column = "A";
                String type = "aggregate";

                budgetSheet.setTotalRemainingForMonth(new BudgetCategory(name, column, row, type));
            }
        }

    }
}