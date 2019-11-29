package com.example.TambahKontak;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;



public class InboxOutbox extends Activity {

	
	ActionBar.Tab TabInbox, TabOutbox;
	Fragment fragmentTabInbox = new FragmentInbox();
	Fragment fragmentTabOutbox = new FragmentOutbox();
	
	private String contact_id, contact_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_messages);

        Intent intent = getIntent();
        
        contact_id = intent.getStringExtra("id");
        contact_name = intent.getStringExtra("name");
        
        Bundle bundle = new Bundle();
        bundle.putString("id", contact_id);
        
        fragmentTabInbox.setArguments(bundle);
        fragmentTabOutbox.setArguments(bundle);
        
        setTitle(contact_name);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        TabInbox = actionBar.newTab().setText("Inbox");
        TabOutbox = actionBar.newTab().setText("Outbox");
        
        TabInbox.setTabListener(new TabListener(fragmentTabInbox));
        TabOutbox.setTabListener(new TabListener(fragmentTabOutbox));
        
		actionBar.addTab(TabInbox);
		actionBar.addTab(TabOutbox);

    }


	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
}