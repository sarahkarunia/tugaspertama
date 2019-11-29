package com.example.TambahKontak;

import java.util.ArrayList;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class FragmentInbox extends Fragment{
	private String TAG = InboxOutbox.class.getSimpleName();
    private ProgressDialog pDialog;
	private TextView Totalinbox;
	private ListView lv;
	
	private static String url = "http://apilearning.totopeto.com/messages/inbox?id=";
	private String Id, Name;
	private ArrayList<HashMap<String, String>> messagelist;
	Button bsend;
	private int Pesanmasuk;
	private String contact_id, contact_name;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inbox, container, false);
        
        messagelist = new ArrayList<HashMap<String, String>>();
        
        lv = (ListView) rootView.findViewById(R.id.messagelist);
        Totalinbox = (TextView) rootView.findViewById(R.id.Totalinbox);
        bsend = (Button) rootView.findViewById(R.id.btkirim); 
        
        //new GetMessages().execute();
        
        lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				HashMap<String, String> hm = messagelist.get(position);
				
				Intent intent = new Intent(getActivity(), BalasActivity.class);
				intent.putExtra("message_id", hm.get("id"));
				//intent.putExtra("contact_name", Name);
				intent.putExtra("contact_id", Id);
				startActivity(intent);
				
			}
		});
        
        bsend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contact_id = getArguments().getString("id");
				contact_name = getArguments().getString("name");
				Intent intent = new Intent(getActivity(), KirimActivity.class);
				intent.putExtra("contact_id", contact_id);
				intent.putExtra("contact_name", contact_name);
				startActivity(intent);
			}
		});
        
        return rootView;
    }
	
	private class GetMessages extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
        	messagelist.clear();
    		String Idcontact = getArguments().getString("id");
    		
    		Id = getArguments().getString("id");
			Name = getArguments().getString("name");
        	
            HttpHandler sh = new HttpHandler();
 
            // String jsonStr = sh.makeServiceCall(url + "1");
            String jsonStr = sh.makeServiceCall(url + Idcontact);
 
            Log.e(TAG, "Response from url: " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray data = jsonObj.getJSONArray("data");
                    Pesanmasuk = data.length();
                    
                    for (int i = 0; i < Pesanmasuk; i++) {
                        JSONObject c = data.getJSONObject(i);
                        
                        String id = c.getString("id");
                        String content = c.getString("content");
                        String created_at = c.getString("created_at");
                        String from = c.getString("from");
                        
                        HashMap<String, String> message = new HashMap<String, String>();
 
                        message.put("id", id);
                        message.put("from", from);
                        message.put("content", content);
                        message.put("created_at", created_at);
                        
                        messagelist.add(message);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
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
            
            Totalinbox.setText(String.valueOf(Pesanmasuk));
            
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), messagelist,
                    R.layout.list_messages, new String[]{"from", "content",
                    "created_at"}, new int[]{R.id.FromTo,
                    R.id.content, R.id.created_at});
 
            lv.setAdapter(adapter);
        }
 
    }
	
	@Override
    public void onResume() {
    	super.onResume();
    	new GetMessages().execute();

	}
}
