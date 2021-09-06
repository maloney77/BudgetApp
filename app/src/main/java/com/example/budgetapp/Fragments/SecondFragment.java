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
import androidx.cardview.widget.CardView;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class SecondFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private BudgetRequestViewModel requestViewModel;
    static final String INPUT_TYPE_ADDITION = "INPUT_TYPE_ADDITION";
    static final String INPUT_TYPE_SUBTRACTION = "INPUT_TYPE_SUBTRACTION";
    TextView currentCategoryValueView = null;
    static final String CURRENT_CATEGORY_TITLE_BASE = "Current Value of ";


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

        //hide the value inputs on load
         view.findViewById(R.id.add_button).setVisibility(View.GONE);
         view.findViewById(R.id.subtract_button).setVisibility(View.GONE);
         view.findViewById(R.id.purchase_input).setVisibility(View.GONE);
         view.findViewById(R.id.change_category_value_title).setVisibility(View.GONE);
         view.findViewById(R.id.category_current_value_card).setVisibility(View.GONE);


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
        categories.stream().filter(cat -> cat.getType().equals("bills")).forEach(cat -> {
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

        Button submitButton = getView().findViewById(R.id.add_button);
        Button subtractButton = getView().findViewById(R.id.subtract_button);
        EditText purchaseInput = getView().findViewById(R.id.purchase_input);
        TextView changeCategoryValueTitle = getView().findViewById(R.id.change_category_value_title);
        //check if the default spinner option is not selected
        if(!parent.getItemAtPosition(pos).toString().equals("Select Budget Category")) {

            //Set the current category page pieces to visible
            submitButton.setVisibility(View.VISIBLE);
            subtractButton.setVisibility(View.VISIBLE);
            purchaseInput.setVisibility(View.VISIBLE);
            changeCategoryValueTitle.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.category_current_value_card).setVisibility(View.VISIBLE);


            //Create map to pull categories by spinner value name
            HashMap<String, BudgetCategory> categoryMap = new HashMap<>();

            requestViewModel.getBudgetSheet().getCategories().forEach(cat -> {
                categoryMap.put(cat.getName(), cat);
            });

            //retrieve selected category from spinner by spinner value
            BudgetCategory selectedCategory = categoryMap.get(parent.getItemAtPosition(pos).toString());

            //Populate UI card with selected category information
            TextView currentCategoryTitle = getView().findViewById(R.id.category_current_value_title);
            currentCategoryTitle.setText(CURRENT_CATEGORY_TITLE_BASE + selectedCategory.getName() + ":");

            currentCategoryValueView = getView().findViewById(R.id.category_current_value);


            //retrieve selected category's value
            final Number[] currentCategoryValue = {0};
            String cellAddressToEdit = selectedCategory.getCategoryMonthCell(LocalDate.now().getMonth().name());

            AsyncTask.execute(() -> {
                try{
                    ValueRange response = requestViewModel.getSheetService().spreadsheets().values()
                            .get(requestViewModel.getBudgetSheet().getSpreadSheetId(), cellAddressToEdit)
                            .execute();

                    String displayCellValue = response.getValues() != null ? response.getValues().get(0).get(0).toString() : "$0.00";
                    currentCategoryValueView.setText(displayCellValue);

                    currentCategoryValue[0] = NumberFormat.getCurrencyInstance(Locale.US).parse(displayCellValue);



                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    Snackbar.make(getView(), "Failed to retrieve the selected cell's data, try refreshing the page" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            //set currentCellValue
            Float currentCellValue = currentCategoryValue[0].floatValue();


            submitButton.setOnClickListener(l -> {
                //check if the value of the cell has been changed since the page has reset, if it has, use the new value stored in the text view
                Boolean currentCellValueHasBeenChanged = null;
                Float convertedValueFromTextView = (float) 0;
                try {
                    convertedValueFromTextView = NumberFormat.getCurrencyInstance(Locale.US).parse(currentCategoryValueView.getText().toString()).floatValue();
                    currentCellValueHasBeenChanged = !convertedValueFromTextView.equals(currentCategoryValue[0].floatValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                updateCellWithNewValue(cellAddressToEdit, purchaseInput.getText().toString(), currentCellValueHasBeenChanged ? convertedValueFromTextView : currentCategoryValue[0].floatValue(), INPUT_TYPE_ADDITION);
                purchaseInput.getText().clear();

            });

            subtractButton.setOnClickListener(l -> {
                //check if the value of the cell has been changed since the page has reset, if it has, use the new value stored in the text view
                Boolean currentCellValueHasBeenChanged = null;
                Float convertedValueFromTextView = (float) 0;
                try {
                    convertedValueFromTextView = NumberFormat.getCurrencyInstance(Locale.US).parse(currentCategoryValueView.getText().toString()).floatValue();
                    currentCellValueHasBeenChanged = !convertedValueFromTextView.equals(currentCategoryValue[0].floatValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                updateCellWithNewValue(cellAddressToEdit, purchaseInput.getText().toString(), currentCellValueHasBeenChanged ? convertedValueFromTextView : currentCategoryValue[0].floatValue(), INPUT_TYPE_SUBTRACTION);
                purchaseInput.getText().clear();
            });
        } else {
            submitButton.setVisibility(View.GONE);
            subtractButton.setVisibility(View.GONE);
            purchaseInput.setVisibility(View.GONE);
        }


    }

//    private Number getCurrentCategoryValue(String cellAddressToRetrieve, String amountToUpdate, String inputType) {
//        final Number[] decimalCellValue = {0};
//        AsyncTask.execute(() -> {
//            try{
//                //get current month
//
//                ValueRange response = requestViewModel.getSheetService().spreadsheets().values()
//                        .get(requestViewModel.getBudgetSheet().getSpreadSheetId(), cellAddressToRetrieve)
//                        .execute();
//
//
//                String displayCellValue = response.getValues() != null ? response.getValues().get(0).get(0).toString() : "$0.00";
//                decimalCellValue[0] = NumberFormat.getCurrencyInstance(Locale.US)
//                        .parse(displayCellValue);
//
//
//            } catch (IOException | ParseException e) {
//                e.printStackTrace();
//            }
//        });
//    }

    private void updateCellWithNewValue(String cellToEdit, String amountToUpdate, Float currentCellValue, String inputType) {


        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        inputType.equals(INPUT_TYPE_ADDITION) ? currentCellValue + Float.parseFloat(amountToUpdate) : currentCellValue - Float.parseFloat(amountToUpdate)
                )
        );
        ValueRange newCellValueRange = new ValueRange().setValues(values);
        String newCellValue = NumberFormat.getCurrencyInstance(Locale.US).format(new BigDecimal(values.get(0).get(0).toString()));

        AsyncTask.execute(() -> {
            try {
                UpdateValuesResponse result =
                        requestViewModel.getSheetService().spreadsheets().values().update(
                                requestViewModel.getBudgetSheet().getSpreadSheetId(), cellToEdit, newCellValueRange)
                                .setValueInputOption("USER_ENTERED")
                                .execute();
                if(result == null || result.getUpdatedCells() == 0){
                    Logger.getLogger("No cells updated");
                    Snackbar.make(getView(), "Failed to update the selected cell's data, please try again" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {
                    currentCategoryValueView.setText(newCellValue);
                    Snackbar.make(getView(), "Update successful, new value: " + newCellValue, Snackbar.LENGTH_LONG)
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