package com.example.budgetapp.Fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.budgetapp.POJO.BudgetCategory;
import com.example.budgetapp.R;
import com.example.budgetapp.ViewModels.BudgetRequestViewModel;
import com.example.budgetapp.ViewModels.CategoryEditViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class SecondFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private BudgetRequestViewModel requestViewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get view model
        requestViewModel = new ViewModelProvider(requireActivity()).get(BudgetRequestViewModel.class);

         view.findViewById(R.id.submit_button).setVisibility(View.GONE);
         view.findViewById(R.id.purchase_input).setVisibility(View.GONE);


        try {
            //create category buttons
            populateCategorySpinner(requestViewModel.getBudgetSheet().getCategories(), view);
//            createCategoryButtons(requestViewModel.getBudgetSheet().getCategories(), view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.button_second).setOnClickListener(view1 ->
                NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

    }

    private void populateCategorySpinner(ArrayList<BudgetCategory> categories, View view) {


        List<String> categoryNames = new ArrayList<String>();
        categoryNames.add("Select Budget Category");
        categories.forEach(cat -> {
            categoryNames.add(cat.getName());
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner categorySpinner = (Spinner) view.findViewById(R.id.category_spinner);

        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        Button submitButton = getView().findViewById(R.id.submit_button);
        EditText purchaseInput = getView().findViewById(R.id.purchase_input);
        if(!parent.getItemAtPosition(pos).toString().equals("Select Budget Category")) {


            submitButton.setVisibility(View.VISIBLE);
            purchaseInput.setVisibility(View.VISIBLE);


            HashMap<String, BudgetCategory> categoryMap = new HashMap<>();

            requestViewModel.getBudgetSheet().getCategories().forEach(cat -> {
                categoryMap.put(cat.getName(), cat);
            });

            BudgetCategory selectedCategory = categoryMap.get(parent.getItemAtPosition(pos).toString());


            submitButton.setOnClickListener(l -> {
                String cellToEdit = selectedCategory.getCategoryMonthCell(LocalDate.now().getMonth().name());

                updateCategoryMonthCell(cellToEdit, purchaseInput.getText().toString());
                purchaseInput.getText().clear();
            });
        } else {
            submitButton.setVisibility(View.GONE);
            purchaseInput.setVisibility(View.GONE);
        }


    }

    private void updateCategoryMonthCell(String cellAddressToRetrieve, String purchaseAmountToAdd) {
        final Number[] decimalCellValue = {0};
        AsyncTask.execute(() -> {
            try{
                //get current month

                ValueRange response = requestViewModel.getSheetService().spreadsheets().values()
                        .get(requestViewModel.getBudgetSheet().getSpreadSheetId(), cellAddressToRetrieve)
                        .execute();


                String displayCellValue = response.getValues() != null ? response.getValues().get(0).get(0).toString() : "$0.00";
                decimalCellValue[0] = NumberFormat.getCurrencyInstance(Locale.US)
                        .parse(displayCellValue);

                updateCellWithNewValue(cellAddressToRetrieve,  purchaseAmountToAdd, decimalCellValue[0].floatValue());


            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
  ;
    }

    private void updateCellWithNewValue(String cellToEdit, String purchaseAmountToAdd, Float currentCellValue) {


        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        currentCellValue + Float.parseFloat(purchaseAmountToAdd)
                )
        );
        ValueRange newCellValue = new ValueRange().setValues(values);
        AsyncTask.execute(() -> {
            try {
                UpdateValuesResponse result =
                        requestViewModel.getSheetService().spreadsheets().values().update(
                                requestViewModel.getBudgetSheet().getSpreadSheetId(), cellToEdit, newCellValue)
                                .setValueInputOption("USER_ENTERED")
                                .execute();
                if(result == null || result.getUpdatedCells() == 0){
                    Logger.getLogger("No cells updated");

                } else {
                    Snackbar.make(getView(), "Update successful, new value: " + newCellValue.getValues().get(0).get(0) , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Logger.getLogger("Successfully updated " + result.getUpdatedCells() + " cells");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }



}