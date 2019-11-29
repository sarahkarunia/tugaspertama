package com.example.TambahKontak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class KirimActivity extends Activity implements OnItemSelectedListener{
	private String TAG = KirimActivity.class.getSimpleName();
	private ProgressDialog pDialog;
    private static String contactsUrl = "http://apilearning.totopeto.com/contacts";
    private static String messagesUrl = "http://apilearning.totopeto.com/messages";
    
    private ArrayList<HashMap<String, String>> contactList;
    private String from_id, to_id, from_name;
	public EditText pesan;
	public Spinner spinner;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kirim);
		Intent intent = getIntent();
		from_id = intent.getStringExtra("contact_id");
		from_name = intent.getStringExtra("contact_name");
		setTitle("Kirim Pesan");

		Button kirimpesan = (Button)findViewById(R.id.btkirim2);
		TextView ke = (TextView)findViewById(R.id.ke);
		TextView isi = (TextView)findViewById(R.id.tpesan);
		pesan = (EditText)findViewById(R.id.edpesan);
		spinner = (Spinner)findViewById(R.id.spinner1);
		spinner.setOnItemSelectedListener(this);
		
		kirimpesan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new postMessage().execute();
				KirimActivity.this.finish();				
			}
		});
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id) {
		
		HashMap<String, String> hm = contactList.get(pos);
		to_id = hm.get("id");
    }

    public void onNothingSelected(AdapterView<?> parent) {
        
    }
	
	private class postMessage extends AsyncTask<Void, Void, Void> {
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KirimActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        
        @Override
        protected Void doInBackground(Void... arg0) {
            
        	String post_params = null;
            JSONObject params = new JSONObject();
 
            try {
            	params.put("from_id", from_id);
            	params.put("to_id", to_id);
            	params.put("content", pesan.getText().toString());
            	post_params = params.toString();
            	
            } catch (JSONException e) {
            	e.printStackTrace();
            }
            
            HttpHandler data = new HttpHandler();
            String jsonStr = data.makePostRequest(messagesUrl, post_params);
            Log.e(TAG, "Response from url: " + jsonStr);
            
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
	}
	
	private class GetContacts extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KirimActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(contactsUrl);
            Log.e(TAG, "Response from url: " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("contacts");
 
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        
                        String id = c.getString("id");
                        String name = c.getString("name");
                        
                        HashMap<String, String> contact = new HashMap<String, String>();
 
                        contact.put("id", id);
                        contact.put("name", name);
                        
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            
            List<String> arrayList = new ArrayList<String>();
            
            for(int i=0; i < contactList.size(); i++) {
            	HashMap<String, String> hm = contactList.get(i);
            	arrayList.add(hm.get("name"));
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(KirimActivity.this,
            		android.R.layout.simple_spinner_item, arrayList); 
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	contactList = new ArrayList<HashMap<String, String>>();
    	new GetContacts().execute();
    }
}


