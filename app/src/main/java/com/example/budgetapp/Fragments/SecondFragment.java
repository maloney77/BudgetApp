package com.example.budgetapp.Fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.budgetapp.POJO.BudgetCategory;
import com.example.budgetapp.POJO.BudgetMonth;
import com.example.budgetapp.R;
import com.example.budgetapp.ViewModels.BudgetRequestViewModel;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class SecondFragment extends Fragment {

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

        try {
            //create category buttons
            createCategoryButtons(requestViewModel.getBudgetSheet().getCategories(), view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void createCategoryButtons(ArrayList<BudgetCategory> categories, View view) throws JSONException {

        //gets constraint layout
        ConstraintLayout layout = view.findViewById(R.id.second_layout);
        //creates constraint set
        ConstraintSet set = new ConstraintSet();

        ArrayList<Integer> buttonIds= new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {

            Button categoryButton = new Button(getContext());

            //sets layout params width as 0 so we can set to match constraint
            categoryButton.setLayoutParams(new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT));

            //generate random view ids
            categoryButton.setId(View.generateViewId());

            //add view id to list for connection logic
            buttonIds.add(categoryButton.getId());
            //sets button text
            categoryButton.setText(categories.get(i).getName());

            Month currentMonth = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentMonth = LocalDate.now().getMonth();
            }

            //grab current month cell letter

            String categoryMonthCell = categories.get(i).getCategoryMonthCell(currentMonth.name());

            categoryButton.setOnClickListener(v -> {
                AsyncTask.execute(() -> {
                    try{
                        //get current month

                        ValueRange response = requestViewModel.getSheetService().spreadsheets().values()
                                .get(requestViewModel.getBudgetSheet().getSpreadSheetId(), categoryMonthCell)
                                .execute();

                        TextView category_amount = view.findViewById(R.id.category_amount);
                        if (response.getValues() != null) {
                            category_amount.setText(response.getValues().get(0).toString());
                        } else {
                            category_amount.setText("Nope");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });

            //add view to layout
            layout.addView(categoryButton,i);

            //idk what this does
            set.clone(layout);

            //set width of button
            set.constrainDefaultWidth(categoryButton.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

            //connection logic: append button to guidline if its the first one, otherwise append to previous button
            if(i ==0){
                set.connect(categoryButton.getId(), ConstraintSet.TOP, R.id.top_guideline, ConstraintSet.BOTTOM, 8);
                set.connect(categoryButton.getId(), ConstraintSet.LEFT, R.id.left_guideline, ConstraintSet.RIGHT, 8);
                set.connect(categoryButton.getId(), ConstraintSet.RIGHT, R.id.middle_guideline, ConstraintSet.LEFT, 8);
            } else if(i == 1) {
                //if even append to previous button
                set.connect(categoryButton.getId(), ConstraintSet.TOP, R.id.top_guideline, ConstraintSet.BOTTOM, 8);

                set.connect(categoryButton.getId(), ConstraintSet.RIGHT, R.id.right_guideline, ConstraintSet.LEFT, 8);

                set.connect(categoryButton.getId(), ConstraintSet.LEFT, R.id.middle_guideline, ConstraintSet.RIGHT);
            }
            else if(i % 2 !=0) {
                //if even append to previous button
                set.connect(categoryButton.getId(), ConstraintSet.TOP, buttonIds.get(i - 2), ConstraintSet.BOTTOM,20);

                set.connect(categoryButton.getId(), ConstraintSet.RIGHT, R.id.right_guideline, ConstraintSet.LEFT, 8);

                set.connect(categoryButton.getId(), ConstraintSet.LEFT, R.id.middle_guideline, ConstraintSet.RIGHT);
            }
            else if(i % 2 ==0){
                set.connect(categoryButton.getId(), ConstraintSet.TOP, buttonIds.get(i - 2), ConstraintSet.BOTTOM,20);
                set.connect(categoryButton.getId(), ConstraintSet.LEFT, R.id.left_guideline, ConstraintSet.RIGHT, 8);
                set.connect(categoryButton.getId(), ConstraintSet.RIGHT, R.id.middle_guideline, ConstraintSet.LEFT, 8);
            }

            //apply set to layout
            set.applyTo(layout);
        }
    }

//    private String getBudgetCategory(ArrayList categories, String targetCategory)
//    {
//        try {
//            cat
//            return categories.get(targetCategory).toString();
//        }catch (Exception e) {
//            System.out.println(e);
//        }
//        return "";
//    }
}