package com.ssgs.tfsaccess;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.ODataConsumer.Builder;
import org.odata4j.consumer.behaviors.BasicAuthenticationBehavior;
import org.odata4j.core.OEntity;
import org.odata4j.format.FormatType;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {
	public static final String EXTRA_MESSAGE = "com.ssgs.tfsaccess";
	ArrayList<String> Projects;
	ArrayAdapter<String> adapter;
	ListView list;
	Button Call;
	String endPoint;
	String domain;
	String userName;
	String passwd;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Projects = new ArrayList<String>();
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.prjlist);
        endPoint = ((EditText) findViewById(R.id.endPoint)).getText().toString();
        domain = ((EditText) findViewById(R.id.Domain)).getText().toString();
        
        
        
        Call = (Button) findViewById(R.id.Button1);
        Call.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        	   	new callService().execute();
        	        }
        	    });
    }
        
        public class callService extends AsyncTask<Void, Void, ArrayList<String>> {

        	protected ProgressDialog progress;
        	
        	@Override
            protected void onPreExecute()
            {
                super.onPreExecute();               
                progress = ProgressDialog.show(MainActivity.this, "", "Getting Project Names from TFS", true, false);
            }

    		@Override
    		protected ArrayList<String> doInBackground(Void... params) {
    		try {
				passwd = ((EditText) findViewById(R.id.Password)).getText().toString();
				    
				userName = domain + "\\" + ((EditText) findViewById(R.id.UName)).getText().toString();
				Builder builder = ODataConsumers.newBuilder(endPoint);
				builder.setClientBehaviors(new BasicAuthenticationBehavior(userName,passwd));
				builder.setFormatType(FormatType.JSON);
				ODataConsumer consumer = builder.build();
				List<OEntity> listEntities = consumer.getEntities("Projects").execute().toList();

				if (listEntities.size() > 0) {
				    for (OEntity entity : listEntities) {
				        Projects.add(entity.getProperty("Name").getValue().toString());    		        
				        }
				    }
			} catch (Exception e) {
				progress.dismiss();
				e.printStackTrace();
			}
    		return Projects;
    		}

    		@Override
    		protected void onPostExecute(ArrayList<String> result) {
    		super.onPostExecute(result);

    		progress.dismiss();
    		adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, result);
    		list.setAdapter(adapter);
    		list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                        long id) {  
                	String item = ((TextView)view).getText().toString();                    
                    Intent intent = new Intent(MainActivity.this, DisplayIterationsActivity.class)  ;                   
                    Bundle bundle = new Bundle();
                    bundle.putString("endPoint",endPoint);
                    bundle.putString("user", userName);
                    bundle.putString("pass", passwd);
                    bundle.putString("Project", item);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });

    		}
    	}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
//   
	
}
