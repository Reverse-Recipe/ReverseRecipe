package reverse.recipe.reverserecipe;

import java.util.ArrayList;
import java.util.Arrays;

import reverse.recipe.reverserecipe.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.os.Build;

public class IngredientSearchActivity extends Activity implements AsyncResponse {

	Button addButton;
	EditText editText;
	ArrayList<String> ingredientList;
	ListView listView;
	ArrayAdapter<String> adapter;
	String[] allIngredients;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ingredient_search);
		
		setupActionBar();
		new GetAllIngredients(this).execute(); //populates auto complete
		
		//declares xml elements
		addButton = (Button)findViewById(R.id.addIngredientButton);
	    editText = (EditText)findViewById(R.id.ingredientsSearch);
	    listView = (ListView)findViewById(R.id.ingredientList);
	    
	    ingredientList = new ArrayList<String>();
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, ingredientList);
	    
	    listView.setAdapter(adapter);
	    addButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				String input = editText.getText().toString();
				
				//adds ingredients to list if they exist in database and are not already on list
				if (Arrays.asList(allIngredients).contains(input) && !(ingredientList.contains(input))) {
			        adapter.add(input);
			    }				
			}   	
	    });
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.ingredient_search, menu);
		return true;
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		
		if (id == R.id.action_settings) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	//Method from AsyncResponse interface
	@Override
	public void responseObtained(String output) {
		
		AutoCompleteTextView autoComplete;
		ArrayAdapter<String> adapter;
		
		allIngredients = Utilities.iterateThroughJson(output,"ingredients", "number of ingredients");
		
		adapter = new ArrayAdapter<String>(IngredientSearchActivity.this,android.R.layout.simple_list_item_1, allIngredients);
		
		//add all ingredients to auto complete
		autoComplete = (AutoCompleteTextView) findViewById(R.id.ingredientsSearch);	
		autoComplete.setAdapter(adapter);
		autoComplete.setThreshold(2); //2 characters before suggesting	
	}
}
