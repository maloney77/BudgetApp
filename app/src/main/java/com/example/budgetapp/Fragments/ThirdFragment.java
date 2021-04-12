package com.example.budgetapp.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.budgetapp.R;
import com.example.budgetapp.ViewModels.BudgetRequestViewModel;
import com.example.budgetapp.ViewModels.CategoryEditViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;


public class ThirdFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private CategoryEditViewModel categoryEditViewModel;
    private BudgetRequestViewModel budgetRequestViewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    public ThirdFragment() {
        // Required empty public constructor
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get view model
        categoryEditViewModel = new ViewModelProvider(requireActivity()).get(CategoryEditViewModel.class);
        budgetRequestViewModel = new ViewModelProvider(requireActivity()).get(BudgetRequestViewModel.class);

        TextView categoryName = view.findViewById(R.id.category_name);
        Button submitButton = view.findViewById(R.id.submit_button);
        EditText purchaseInput = view.findViewById(R.id.purchase_input);
        categoryName.setText(categoryEditViewModel.getBudgetCategory().getName());

        submitButton.setOnClickListener(v -> {
//                NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(Locale.US);
//                currencyInstance.format(purchaseInput.getText());
                    List<List<Object>> values = Arrays.asList(
                            Arrays.asList(
                                    categoryEditViewModel.getCategoryValue().floatValue() + Float.parseFloat(purchaseInput.getText().toString())
                            )
                            // Additional rows ...
                    );
                    ValueRange newCellValue = new ValueRange().setValues(values);
            AsyncTask.execute(() -> {
                    try {
                        UpdateValuesResponse result =
                                budgetRequestViewModel.getSheetService().spreadsheets().values().update(
                                        budgetRequestViewModel.getBudgetSheet().getSpreadSheetId(), categoryEditViewModel.getCellToEdit(), newCellValue)
                                        .setValueInputOption("USER_ENTERED")
                                        .execute();
                        if(result == null || result.getUpdatedCells() == 0){
                            Logger.getLogger("No cells updated");

                        } else {
                            Snackbar.make(view, "Update successful, new value: " + newCellValue.getValues().get(0).get(0) , Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Logger.getLogger("Successfully updated " + result.getUpdatedCells() + " cells");

                        }
                        purchaseInput.getText().clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            });



            }
        );

        view.findViewById(R.id.previous_nav).setOnClickListener(view1 ->
                NavHostFragment.findNavController(ThirdFragment.this)
                .navigate(R.id.action_ThirdFragment_to_SecondFragment)
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }




}