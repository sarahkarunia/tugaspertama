package com.example.TambahKontak;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BalasActivity extends Activity {
	
	private String TAG = AddContact.class.getSimpleName();
    private ProgressDialog pDialog;
    
    private static String messageUrl = "http://apilearning.totopeto.com/messages/inbox?id=";
    private static String sendUrl = "http://apilearning.totopeto.com/messages";
	
    TextView isiPesan, pesanMasuk;
	EditText entriPesan;
	Button kirim;
	
	private String from_id, to_id, from_name, to_name, to_content, message_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.balas);
		
		// Data of sender
		Intent intent = getIntent();
		from_id = intent.getStringExtra("contact_id");
		from_name = intent.getStringExtra("contact_name");
		message_id = intent.getStringExtra("message_id");		
		
		isiPesan = (TextView) findViewById(R.id.isipesan);
		pesanMasuk = (TextView) findViewById(R.id.pesanmasuk);
		entriPesan = (EditText) findViewById(R.id.entripesan);
		kirim = (Button) findViewById(R.id.btsend);		
		
		kirim.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SendMessage().execute();
				BalasActivity.this.finish();				
			}
		});
	}
	
	private class SendMessage extends AsyncTask<Void, Void, Void> {
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BalasActivity.this);
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
            	params.put("content", entriPesan.getText().toString());
            	post_params = params.toString();
            	
            } catch (JSONException e) {
            	e.printStackTrace();
            }
            
            HttpHandler data = new HttpHandler();
            String jsonStr = data.makePostRequest(sendUrl, post_params);
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
	
	private class GetMessage extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BalasActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(messageUrl + from_id);
            Log.e(TAG, "Response from url: " + jsonStr);
 
            if (jsonStr != null) {
                try {
                	JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray data = jsonObj.getJSONArray("data");
                    
                    // Loop until the correct message ID shows up
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        
                        String c_message_id = c.getString("id");
                        if(Integer.parseInt(message_id) == Integer.parseInt(c_message_id)) {
                        	to_id = c.getString("from_id");
                            to_content = c.getString("content");
                            to_name = c.getString("from");
                            break;
                        }
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

            if (pDialog.isShowing())
                pDialog.dismiss();
            
            isiPesan.setText("Isi Pesan");
            pesanMasuk.setText(to_content);
            setTitle("Balas ke " + to_name);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	new GetMessage().execute();
    }
}
