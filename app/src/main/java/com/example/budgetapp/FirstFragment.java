package com.example.budgetapp;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.logging.Logger;

public class FirstFragment extends Fragment {

    private BudgetRequestViewModel requestViewModel;

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
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        System.out.println("got response");
                        JSONArray categories = new JSONArray();
                        // attempt to convert string to json array
                        try {
                            categories = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Logger.getLogger("Thing");
                        //set categories for second fragment
                        requestViewModel.setCategories(categories);
                        NavHostFragment.findNavController(FirstFragment.this)
                                .navigate(R.id.action_FirstFragment_to_SecondFragment);
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.getLogger("That didn't work!");
                }
            });

            requestQueue.add(stringRequest);
        });


    }




}