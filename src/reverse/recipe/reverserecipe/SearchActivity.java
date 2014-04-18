package reverse.recipe.reverserecipe;


import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import reverse.recipe.reverserecipe.R;
import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class SearchActivity extends ListActivity implements AsyncResponse {
	
	recipeArrayAdapter adapter;
	ArrayList<String> searchIngredients;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//get ingredients selected from pantry and execute search
		if (getIntent().hasExtra("searchTerms")){
			try {
				searchWithIntentExtras();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//Method Called By Search Recipe Button Click
	public void searchRecipes(View view) {
		
		//Create List Adapter for Results
		ArrayList<Recipe> arrayOfRecipes = new ArrayList<Recipe>();
		// Create the adapter to convert the array to views
		adapter = new recipeArrayAdapter(this, arrayOfRecipes);
		// Attach the adapter to a ListView
		setListAdapter(adapter);
		
		String recipeSearchStr;
		recipeSearchStr = "http://www.reverserecipe.host22.com/api/?tag=searchRecipes&ingreds="; //Default Web Address
		recipeSearchStr += "cheese%2A+bacon"; //Add Ingredients Here
		new GetRecipeSearchResults(this).execute(recipeSearchStr); //Execute Search
	}
	
	public void searchWithIntentExtras() throws UnsupportedEncodingException{
		
		Bundle bundle = getIntent().getExtras();
		ArrayList<Recipe> arrayOfRecipes = new ArrayList<Recipe>();
		String recipeSearchStr;
		
		searchIngredients = bundle.getStringArrayList("searchTerms");
		adapter = new recipeArrayAdapter(this, arrayOfRecipes);
		setListAdapter(adapter);
		ListView list = getListView();
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
				
				Intent intent = new Intent(SearchActivity.this, DisplayRecipeActivity.class);
				Bundle bundle = new Bundle();
				
				//we will have a better way to do this, was just fast solution
				TextView extrasView = (TextView) view.findViewById(R.id.extrasLabel);
				String extrasText = extrasView.getText().toString();
				
				String[] extraSections = extrasText.split("         Relevance");
				String recipeId = extraSections[0].replaceAll("\\D+","");
				//end bad solution
				
				bundle.putString("recipeId", recipeId);
				
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		
		
		recipeSearchStr = "http://www.reverserecipe.host22.com/api/?tag=searchRecipes&ingreds="; 
		recipeSearchStr += Utilities.arrayToSearchString(searchIngredients);
		
		new GetRecipeSearchResults(this).execute(recipeSearchStr);
	}


	//Stores Recipes Found
	public class Recipe { 
		String Title;
		int ID;
		String Author;
		double Relevance;
		
	    public Recipe(String titleT, int idT, String authorT, double relevanceT) {
	        this.Title = titleT;
	        this.ID = idT;
	        this.Author = authorT;
	        
	        DecimalFormat dec = new DecimalFormat("0.00");
	        this.Relevance = Double.parseDouble(dec.format(relevanceT)); //Round to 2 decimals
	     }
	}


	@Override
	public void responseObtained(String output) {
		
		try {
			//parse JSON
			JSONObject resultObject = new JSONObject(output); //Puts String Retrieved In JSONObject
			JSONObject resultObject2 = resultObject.getJSONObject("recipes"); //Refines Object To Recipes

			int NumRecipes = resultObject.getInt("number of recipes"); // Grabs number of recipes

			//loop through recipes
			for (int p=1; p<=NumRecipes; p++) {

				try{
					//attempt to retrieve place data values
					JSONObject recipeObject = resultObject2.getJSONObject(String.valueOf(p)); //Get first object from array

					String titleTemp = recipeObject.getString("title"); //Get Recipe Title
					int idTemp = recipeObject.getInt("recipe id");
					String authorTemp = recipeObject.getString("author");
					double relevanceTemp = recipeObject.getDouble("relevance");
					
					//Display Recipe In List
					Recipe newRecipe = new Recipe(titleTemp, idTemp, authorTemp, relevanceTemp);
					adapter.add(newRecipe);
				}
				catch(JSONException jse){
					jse.printStackTrace();
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
