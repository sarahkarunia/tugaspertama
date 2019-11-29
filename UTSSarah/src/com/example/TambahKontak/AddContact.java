package com.example.TambahKontak;


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



public class AddContact extends Activity {
	EditText enama, ephone, eemail, ealamat, edob;
	Button bsave;
		private String TAG = MainActivity.class.getSimpleName();
	    private ProgressDialog pDialog;
		//public String response_url;
		private static String url = "http://apilearning.totopeto.com/contacts";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tambahkontak);
		
		enama = (EditText)findViewById(R.id.ednama);
		ealamat = (EditText)findViewById(R.id.edalamat);
		eemail = (EditText)findViewById(R.id.edemail);
		ephone = (EditText)findViewById(R.id.edno);
		edob = (EditText)findViewById(R.id.eddob);
		bsave = (Button)findViewById(R.id.bsave);
		
		bsave.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
					//Toast toast=Toast.makeText(getApplicationContext(), "DIKERJAKAN KELOMPOK LAIN!!!", Toast.LENGTH_LONG);toast.show();	

					Intent intent = new Intent(AddContact.this, MainActivity.class);
					new SimpanKontak().execute();
					AddContact.this.finish();
					startActivity(intent);

				}
			});
		}
			private class SimpanKontak extends AsyncTask<Void, Void, Void> {
				
			     protected void onPreExecute(){
			            super.onPreExecute();
			            // Showing progress dialog
			            pDialog = new ProgressDialog(AddContact.this);
			            pDialog.setMessage("Please wait...");
			            pDialog.setCancelable(false);
			            pDialog.show();
			 
			        }
				
				protected void onPostExecute(Void result){
					super.onPostExecute(result);
					if(pDialog.isShowing()){pDialog.dismiss();}
					AddContact.this.finish();
				}

				protected Void doInBackground(Void... arg0) {
					 String post_params = null;
					 JSONObject params = new JSONObject();
					 try {
						 params.put("name", enama.getText().toString());
						 params.put("address", ealamat.getText().toString());
						 params.put("email", eemail.getText().toString());
						 params.put("phone", ephone.getText().toString());
						 params.put("dob", edob.getText().toString());
						 post_params = params.toString();
						 }catch (JSONException e){
						 e.printStackTrace();
						 }
						 HttpHandler data = new HttpHandler();
						 String jsonStr = data.makePostRequest(url, post_params);
						/* Log.e(TAG, "Response from URL: " + jsonStr);
						 response_url = jsonStr;*/
						 return null; 
				 }
		       
		  }
	
}
