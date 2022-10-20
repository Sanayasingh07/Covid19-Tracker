package com.example.covid_19tracker;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import java.util.regex.*;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    public static List<Model> modelList = new ArrayList<>();
    Model model;
    Adapter adapter;
    EditText state,district;
    Button btnSearch;

    //converting first letter or each word to capital
    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        btnSearch=findViewById(R.id.button1);
        state=findViewById(R.id.EditText1);
        district=findViewById(R.id.EditText2);




        //click listner
       btnSearch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
               String url = "https://data.covid19india.org/state_district_wise.json";
               JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, url,null,new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {


                       try {
                           //JSONObject object = new JSONObject(response);
                           // From that object we are fetching data
                           JSONObject object1 = response.getJSONObject(capitalize((state.getText().toString()).trim()));
                           JSONObject object2 = object1.getJSONObject("districtData");
                           JSONObject object3 = object2.getJSONObject(capitalize((district.getText().toString()).trim()));
                           JSONObject object4 = object3.getJSONObject("delta");


                           String active = object3.getString("active");
                           String confirmed = object3.getString("confirmed");
                           String deceased = object3.getString("deceased");
                           String recovered = object3.getString("recovered");

                           String confInc = object4.getString("confirmed");
                           String confDec = object4.getString("deceased");
                           String confRec = object4.getString("recovered");

                           model = new Model(capitalize((district.getText().toString()).trim()), confirmed, deceased, recovered, active,
                                   confInc, confDec, confRec);
                           // placing data into the app using AdapterClass
                           modelList.add(model);

                           adapter = new Adapter(MainActivity.this, modelList);
                           listView.setAdapter(adapter);

                          // Toast.makeText(MainActivity.this,"state: " +recovered,Toast.LENGTH_SHORT).show();

                       } catch (JSONException e){
                           e.printStackTrace();
                       }


                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Toast.makeText(MainActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                   }
               });
               queue.add(request);


           }
       });


    }

}
