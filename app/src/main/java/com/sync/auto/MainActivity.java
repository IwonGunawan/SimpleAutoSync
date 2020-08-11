package com.sync.auto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String URL_NAME = "http://192.168.0.105/simple_auto_sync/students.php";
    public static final int SYNC_WITH_SERVER = 1;
    public static final int NO_SYNC_WITH_SERVER = 0;
    public static final String DATA_SAVED_BROADCAST = "com.sync.datasaved";


    private DatabaseHelper db;
    private EditText etName;
    private Button btnSave;
    private ListView listView;

    // list Store all name
    private List<NameModel> nameList;

    private BroadcastReceiver broadcastReceiver;
    private NameAdapter nameAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initializing views and object
        db = new DatabaseHelper(this);
        nameList = new ArrayList<>();

        etName = findViewById(R.id.et_name);
        btnSave = findViewById(R.id.btn_save);
        listView = findViewById(R.id.list_view);

        btnSave.setOnClickListener(this);

        // calling method to load all stored name
        loadNamelist();

        // broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadNamelist();
            }
        };

        // registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            saveNameToServer();
        }
    }

    public void loadNamelist() {
        nameList.clear();
        Cursor cursor = db.getName();

        if (cursor.moveToFirst()) {
            do{
                NameModel name = new NameModel(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                nameList.add(name);

            } while (cursor.moveToNext());
        }

        nameAdapter = new NameAdapter(this, R.layout.activity_item, nameList);
        listView.setAdapter(nameAdapter);

    }

    private void saveNameToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving name..");
        progressDialog.show();

        final String name = etName.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                saveNameToLocal(name, SYNC_WITH_SERVER);
                            }
                            else {
                                saveNameToLocal(name, NO_SYNC_WITH_SERVER);
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        saveNameToLocal(name, NO_SYNC_WITH_SERVER);

                    }
                }
        ){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                return params;

            }
        };

        //process send to server
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void saveNameToLocal(String name, int status) {
        etName.setText("");
        db.addName(name, status);

        // added to listing
        NameModel nameModel = new NameModel(name, status);
        nameList.add(nameModel);
        refrestList();

    }



    private void refrestList() {
        nameAdapter.notifyDataSetChanged();
    }


}
