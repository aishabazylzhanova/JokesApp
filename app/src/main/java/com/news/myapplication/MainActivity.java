package com.news.myapplication;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // creating variable for recycler view,
    // adapter, array list, progress bar
    private RecyclerView currencyRV;
    private EditText searchEdt;
    private Button button;
    private ArrayList<CurrencyModal> currencyModalArrayList;
    private CurrencyRVAdapter currencyRVAdapter;
    private ProgressDialog loadingBar;
    int page=1;
    private Switch themeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("tag", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchEdt = findViewById(R.id.idEdtCurrency);
        button = findViewById(R.id.button);
        loadingBar = new ProgressDialog(this);
        themeSwitch = findViewById(R.id.theme);


        // initializing all our variables and array list.

        currencyRV = findViewById(R.id.idRVcurrency);
        currencyModalArrayList = new ArrayList<>();

        // initializing our adapter class.
        currencyRVAdapter = new CurrencyRVAdapter(currencyModalArrayList, this);

        // setting layout manager to recycler view.
        currencyRV.setLayoutManager(new LinearLayoutManager(this));

        // setting adapter to recycler view.
        currencyRV.setAdapter(currencyRVAdapter);
        getData();
        // calling get data method to get data from API.

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("tag", "onStart");

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // on below line calling a
                // method to filter our array list
                filter(s.toString());
            }
        });

    }

    @Override
    protected void onResume() {
        Log.e("tag", "onResume");
        super.onResume();
        // on below line we are adding text watcher for our
        // edit text to check the data entered in edittext.

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page=page+1;
                getData();

            }
        });
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                } else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
            }
        });

    }

    @Override
    protected void onPause() {
        Log.e("tag", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("tag", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("tag", "onDestroy");

    }

    private void filter(String filter) {
        // on below line we are creating a new array list
        // for storing our filtered data.
        ArrayList<CurrencyModal> filteredlist = new ArrayList<>();
        // running a for loop to search the data from our array list.
        for (CurrencyModal item : currencyModalArrayList) {
            // on below line we are getting the item which are
            // filtered and adding it to filtered list.
            if (item.getName().toLowerCase().contains(filter.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        // on below line we are checking
        // weather the list is empty or not.
        if (!filteredlist.isEmpty()) {
            // on below line we are calling a filter
            // list method to filter our list.
            currencyRVAdapter.filterList(filteredlist);
        }
    }

    private void getData() {
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please, wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        // creating a variable for storing our string.
        String url = "https://icanhazdadjoke.com/search?limit=30&page="+page;
        // creating a variable for request queue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // making a json object request to fetch data from API.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // inside on response method extracting data
                // from response and passing it to array list
                // on below line we are making our progress
                // bar visibility to gone.

                try {
                    // extracting data from json.
                    JSONArray dataArray = response.getJSONArray("results");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);

                        String name = dataObj.getString("joke");

                        // adding all data to our array list.
                        currencyModalArrayList.add(new CurrencyModal(name));
                    }
                    // notifying adapter on data change.
                    currencyRVAdapter.notifyDataSetChanged();
                    loadingBar.dismiss();

                } catch (JSONException e) {
                    // handling json exception.
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something went amiss. Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // displaying error response when received any error.
                Toast.makeText(MainActivity.this, "Something went amiss. Please try again later", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                // in this method passing headers as
                // key along with value as API keys.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                // at last returning headers
                return headers;
            }
        };
        // calling a method to add our
        // json object request to our queue.
        queue.add(jsonObjectRequest);
    }
}
