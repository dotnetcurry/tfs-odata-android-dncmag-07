package com.ssgs.tfsaccess;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.ODataConsumer.Builder;
import org.odata4j.consumer.behaviors.BasicAuthenticationBehavior;
import org.odata4j.core.*;
import org.odata4j.format.FormatType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DisplayIterationsActivity extends Activity {

	String project;
	ArrayList<String> titles;
	ListView list1;
	Button Call1;
	ArrayAdapter<String> adapter;
	String endPoint;
	String userName;
	String passwd;
	@SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Get the message from the intent
        Intent intent = getIntent();
        
        project = intent.getStringExtra("Project");
        endPoint = intent.getStringExtra("endPoint");
        userName = intent.getStringExtra("user");
        passwd = intent.getStringExtra("pass");
        
        list1 = (ListView) findViewById(R.id.wiList);
        
        new GetProjectDataService().execute();
      
	}
	
	public class GetProjectDataService extends AsyncTask<Void, Void, ArrayList<String>>
	{

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			//android.os.Debug.waitForDebugger();
			try {
				Builder builder = ODataConsumers.newBuilder(endPoint);
				builder.setClientBehaviors(new BasicAuthenticationBehavior(userName,passwd));
				builder.setFormatType(FormatType.JSON);
				ODataConsumer consumer = builder.build();
				titles = new ArrayList<String>();
				
				List<OEntity> listEntities = consumer.getEntities("IterationPaths").execute().toList();

				if (listEntities.size() > 0) {
				    for (OEntity entity : listEntities) {
				    	if (entity.getProperty("Path").getValue().toString().contains(project + "<")){
				    		titles.add(entity.getProperty("Path").getValue().toString());
				    	}
				    }
				}
			} catch (Exception e) {
				
			}
			return titles;
			
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
		super.onPostExecute(result);

			adapter = new ArrayAdapter<String>(DisplayIterationsActivity.this,android.R.layout.simple_list_item_1, result);
			list1.setAdapter(adapter);
			list1.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                        long id) {  
                	String item = ((TextView)view).getText().toString();                    
                    Intent intent = new Intent(DisplayIterationsActivity.this, WorkItemsActivity.class)  ;                   
                    Bundle bundle = new Bundle();
                    bundle.putString("endPoint",endPoint);
                    bundle.putString("user", userName);
                    bundle.putString("pass", passwd);
                    bundle.putString("Project", project);
                    bundle.putString("iteration", item);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });
		}
	}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
