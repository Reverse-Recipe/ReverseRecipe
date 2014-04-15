package reverse.recipe.reverserecipe;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;

import com.google.gson.Gson;

import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.os.Build;

public class MyPantryActivity extends Activity {
	
	Button addButton;
	Button deleteButton;
	EditText editText;
	ArrayList<String> pantryList;
	String savedPantry;
	ListView listView;
	ArrayAdapter<String> adapter;
	String[] allIngredients;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_pantry);
		setupActionBar();
		
		//gets items already saved
		prefs = this.getSharedPreferences("reverseRecipe", Context.MODE_PRIVATE);
		savedPantry = prefs.getString("reverseRecipe.savedPantry", "Didnt work");
		pantryList = new ArrayList<String>();
		
	    try {
			pantryList = Utilities.jsonStringToArray(savedPantry);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		new GetAllIngredients().execute(); //populates auto complete
		
		addButton = (Button)findViewById(R.id.addIngredientButton);
		deleteButton = (Button)findViewById(R.id.deleteButton);
	    editText = (EditText)findViewById(R.id.ingredientsSearch);
	    listView = (ListView)findViewById(R.id.ingredientList);
	    
	    //puts saved pantry ingredients into list view
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, pantryList);   
	    listView.setAdapter(adapter);
	    
	    addButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				String input = editText.getText().toString();
				
				if (Arrays.asList(allIngredients).contains(input) && !(pantryList.contains(input))) {
			        adapter.add(input);	        
			        String pantryObject = new Gson().toJson(pantryList);       
			        prefs.edit().putString("reverseRecipe.savedPantry", pantryObject).commit(); 
			        adapter.notifyDataSetChanged();
			    }				
			}   	
	    });
	    
	    deleteButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
 
                for(int i = listView.getCount() - 1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                        adapter.remove(pantryList.get(i));
                    }
                }
                
                checkedItemPositions.clear();
                adapter.notifyDataSetChanged();
                String pantryObject = new Gson().toJson(pantryList);       
		        prefs.edit().putString("reverseRecipe.savedPantry", pantryObject).commit(); 		
			}    	
	    });
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my_pantry, menu);
		return true;
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
	
	private class GetAllIngredients extends AsyncTask<String, Void, String>{
		
		protected String doInBackground(String... id) {
			
			String ingredientsURL = "http://www.reverserecipe.host22.com/api/?tag=getAllIngredients";

			return Utilities.fetchData(ingredientsURL).toString();
		}
		
		protected void onPostExecute(String info) {
			super.onPostExecute(info);
			
			AutoCompleteTextView autoComplete;
			ArrayAdapter<String> adapter;
			
			allIngredients = Utilities.iterateThroughJson(info,"ingredients", "number of ingredients");
			
			adapter = new ArrayAdapter<String>(MyPantryActivity.this,android.R.layout.simple_list_item_1, allIngredients);
			
			autoComplete = (AutoCompleteTextView) findViewById(R.id.ingredientsSearch);	
			autoComplete.setAdapter(adapter);
			autoComplete.setThreshold(2);
			
		}
	}

}
