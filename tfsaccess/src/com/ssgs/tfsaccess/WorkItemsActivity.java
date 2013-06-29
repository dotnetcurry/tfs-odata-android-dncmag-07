package com.ssgs.tfsaccess;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.ODataConsumer.Builder;
import org.odata4j.consumer.behaviors.BasicAuthenticationBehavior;
import org.odata4j.core.OEntity;
import org.odata4j.format.FormatType;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class WorkItemsActivity extends Activity {

	TextView T42, T43, T52, T53, T62, T63;
	String project, endPoint, userName, passwd, iteration;
	int i42 = 0,i43 = 0,i52 = 0,i53 = 0,i62 = 0,i63 = 0;
	static ArrayList<String> numbers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_items);
		// Get the message from the intent
        Intent intent = getIntent();
        numbers = new ArrayList<String>();
        project = intent.getStringExtra("Project");
        endPoint = intent.getStringExtra("endPoint");
        userName = intent.getStringExtra("user");
        passwd = intent.getStringExtra("pass");
        iteration = intent.getStringExtra("iteration");
        
        T42 = (TextView)findViewById(R.id.T42);
        T43 = (TextView)findViewById(R.id.T43);
        T52 = (TextView)findViewById(R.id.T52);
        T53 = (TextView)findViewById(R.id.T53);
        T62 = (TextView)findViewById(R.id.T62);
        T63 = (TextView)findViewById(R.id.T63);
        new GetIterationDataService().execute();

	}    
        class GetIterationDataService extends AsyncTask<Void, Void, ArrayList<String>>
    	{
        	
    		@Override
    		protected ArrayList<String> doInBackground(Void... params) {
//    			android.os.Debug.waitForDebugger();
    			try {
    				Builder builder = ODataConsumers.newBuilder(endPoint);
    				builder.setClientBehaviors(new BasicAuthenticationBehavior(userName,passwd));
    				builder.setFormatType(FormatType.JSON);
    				ODataConsumer consumer = builder.build();
    				iteration = iteration.replace("<", "\\");
    				String StrFilter = "IterationPath eq '" + iteration + "'";
    				
    				List<OEntity> listEntities = consumer.getEntities("WorkItems").filter(StrFilter).execute().toList();

    				if (listEntities.size() > 0) {
    				    for (OEntity entity : listEntities) {
    				    	String WiType = entity.getProperty("Type").getValue().toString();
    				    	Log.d("WorkItemType", WiType);
    				    	String WiState = entity.getProperty("State").getValue().toString();
    				    	Log.d("WorkItemState", WiState);
    				    	if (WiType.contains("Requirement") ||
    				    			WiType.contains("User Story")	|| 
    				    			WiType.contains("Product Backlog Item")){
    				    		if (WiState.contains("Closed") || WiState.contains("Done")){
    				    			i43++;
       				    		}
    				    		else {
    				    			i42++;
    				    		}
    				    	}
    				    	else if (WiType.contains("Task")){
    				    		if (WiState.contains("Closed")){
    				    			i53++;
       				    		}
    				    		else {
    				    			i52++;
    				    		}
    				    	}
    				    	else if (WiType.contains("Bug")){
    				    		if (WiState.contains("Closed")){
    				    			i63++;
       				    		}
    				    		else {
    				    			i62++;
    				    		}
    				    	}
    				    }
    				}
    				
    				numbers.add(Integer.toString(i42));
    				numbers.add(Integer.toString(i43));
    				numbers.add(Integer.toString(i52));
    				numbers.add(Integer.toString(i53));
    				numbers.add(Integer.toString(i62));
    				numbers.add(Integer.toString(i63));
    				
    				
    				
    			} catch (Exception e) {
    				
    			}
    			return numbers;
    			
    		}
    		
    		@Override
    		protected void onPostExecute(ArrayList<String> result) {
    		super.onPostExecute(result);
    		T42.setText(numbers.get(0));
    		T43.setText(numbers.get(1));
    		T52.setText(numbers.get(2));
    		T53.setText(numbers.get(3));
    		T62.setText(numbers.get(4));
    		T63.setText(numbers.get(5));

    			
    		}
    	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.work_items, menu);
		return true;
	}

}
