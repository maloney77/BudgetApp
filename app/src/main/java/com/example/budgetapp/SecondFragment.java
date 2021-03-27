package com.example.budgetapp;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            createCategoryButtons(requestViewModel.getCategories(), view);
        } catch (JSONException e) {
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
    public void createCategoryButtons(JSONArray categories, View view) throws JSONException {

        //gets constraint layout
        ConstraintLayout layout = view.findViewById(R.id.second_layout);
        //creates constraint set
        ConstraintSet set = new ConstraintSet();

        ArrayList<Integer> buttonIds= new ArrayList<>();
        for (int i = 0; i < categories.length(); i++) {

            Button categoryButton = new Button(getContext());

            //sets layout params width as 0 so we can set to match constraint
            categoryButton.setLayoutParams(new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT));

            //generate random view ids
            categoryButton.setId(View.generateViewId());

            //add view id to list for connection logic
            buttonIds.add(categoryButton.getId());
            //sets button text
            categoryButton.setText(getBudgetCategory(categories.getJSONObject(i), "name"));

            //add view to layout
            layout.addView(categoryButton,i);

            //idk what this does
            set.clone(layout);

            //set width of button
            set.constrainDefaultWidth(categoryButton.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

            //connection logic: append button to guidline if its the first one, otherwise append to previous button
            if(i < 1){
                set.connect(categoryButton.getId(), ConstraintSet.TOP, R.id.guideline, ConstraintSet.BOTTOM, 8);
            }else{
                set.connect(categoryButton.getId(), ConstraintSet.TOP, buttonIds.get(i - 1), ConstraintSet.BOTTOM);
            }

            //apply set to layout
            set.applyTo(layout);
        }
    }

    private String getBudgetCategory(JSONObject categories, String targetCategory)
    {
        try {
            return categories.get(targetCategory).toString();
        }catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }
}