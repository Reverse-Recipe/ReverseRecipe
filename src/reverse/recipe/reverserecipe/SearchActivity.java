package reverse.recipe.reverserecipe;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import reverse.recipe.reverserecipe.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

public class SearchActivity extends ListActivity implements AsyncResponse {

	recipeArrayAdapter adapter;
	ArrayList<String> searchIngredients;
	ArrayList<Recipe> arrayOfRecipes;
	boolean[] hasImage; //Stores which recipes have an image (to load later)
	Bitmap defaultImage;
	ProgressDialog loadingDialog;
	Button filterButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		setupActionBar();

		//get ingredients selected from pantry and execute search
		if (getIntent().hasExtra("searchTerms")){
			try {
				if (isOnline()) {
					searchWithIntentExtras();
					
					filterButton = (Button)findViewById(R.id.filterButton);
					
					filterButton.setOnClickListener(new Button.OnClickListener(){
			    		
			    		@Override
			    	   public void onClick(View arg0) {
			    			showFilterDialog();
				   }}); 
				}
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
		getMenuInflater().inflate(R.menu.main_menu_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_home:
			Intent homeIntent = new Intent(this,MainActivity.class);
			startActivity(homeIntent);
			return true;
		case R.id.action_cookbook:
			Intent cookbookIntent = new Intent(this,CookBookActivity.class);
			startActivity(cookbookIntent);
			return true;
		case R.id.action_analytics:
			Intent analyticsIntent = new Intent(this,AnalyticsActivity.class);
			startActivity(analyticsIntent);
			return true;
		case R.id.action_shopping_list:
			Intent shoppinglistIntent = new Intent(this,ShoppingListActivity.class);
			startActivity(shoppinglistIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	public void searchWithIntentExtras() throws UnsupportedEncodingException{

		loadingDialog = new ProgressDialog(SearchActivity.this);
		loadingDialog.setMessage("Finding Your Results");
		loadingDialog.show();

		Bundle bundle = getIntent().getExtras();
		arrayOfRecipes = new ArrayList<Recipe>();
		String recipeSearchStr;

		searchIngredients = bundle.getStringArrayList("searchTerms");
		
		DbHelper db = new DbHelper(this);
		
		for (int i = 0; i < searchIngredients.size(); i++){
			db.addIngredient(searchIngredients.get(i));
			db.addToIngredientCount(searchIngredients.get(i));
		}
		
		adapter = new recipeArrayAdapter(this, arrayOfRecipes);
		setListAdapter(adapter);
		defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.food_network_logo);

		ListView list = getListView();
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

				Intent intent = new Intent(SearchActivity.this, DisplayRecipeActivity.class);
				Bundle bundle = new Bundle();

				String recipeId = String.valueOf(arrayOfRecipes.get(pos).ID); 
				bundle.putString("recipeId", recipeId);

				intent.putExtras(bundle);
				startActivity(intent);
			}});


		recipeSearchStr = "http://www.reverserecipe.host22.com/api/?tag=searchRecipes&include="; 
		recipeSearchStr += Utilities.arrayToSearchString(searchIngredients);
		recipeSearchStr += "&exclude="; //not used for now

		new GetRecipeSearchResults(this).execute(recipeSearchStr);
	}


	//Stores Recipes Found
	public class Recipe { 
		String Title;
		int ID;
		String Author;
		double Relevance;
		Bitmap Image;
		String imageURL;
		int Time;
		String Difficulty;
		int Rating;

		public Recipe(String titleT, int idT, String authorT, double relevanceT, String imageURLT, Bitmap imageT, String difficultyT, int timeT, int ratingT) {
			this.Title = titleT;
			this.ID = idT;
			if ("NULL".equals(authorT)) {
				authorT = "Not Available";
			}
			this.Author = authorT;

			DecimalFormat dec = new DecimalFormat("0.00");
			this.Relevance = Double.parseDouble(dec.format(relevanceT)); //Round to 2 decimals

			if ("NULL".equals(difficultyT)) {
				difficultyT = "Not Available";
			}
			this.Difficulty = difficultyT;
			
			this.Time = timeT;
			
			this.imageURL = imageURLT;
			this.Image = imageT;
			
			this.Rating = ratingT;
		}
	}


	@Override
	public void responseObtained(String output) {

		try {
			//parse JSON
			JSONObject resultObject = new JSONObject(output); //Puts String Retrieved In JSONObject
			JSONObject resultObject2 = resultObject.getJSONObject("recipes"); //Refines Object To Recipes

			int NumRecipes = resultObject.getInt("number of recipes"); // Grabs number of recipes
			hasImage = new boolean[NumRecipes];
					
			//loop through recipes
			for (int p=1; p<=NumRecipes; p++) {

				try{
					//attempt to retrieve place data values
					JSONObject recipeObject = resultObject2.getJSONObject(String.valueOf(p)); //Get first object from array

					String titleTemp = recipeObject.getString("title"); //Get Recipe Title
					int idTemp = recipeObject.getInt("recipe id");
					String authorTemp = recipeObject.getString("author");
					double relevanceTemp = recipeObject.getDouble("relevance");
					String difficultyTemp = recipeObject.getString("difficulty");
					String imageURLTemp = recipeObject.getString("image").replace("\\/", "/");
					int timeTemp = recipeObject.getInt("cookTime") + recipeObject.getInt("prepTime");
					int ratingTemp = recipeObject.getInt("rating");
					
					//Mark Recipe's That Need Their Image Downloaded
					if (!"NULL".equals(imageURLTemp)) {
						hasImage[p-1] = true;
					} else {
						hasImage[p-1] = false;
					}

					//Display Recipe In List
					Recipe newRecipe = new Recipe(titleTemp, idTemp, authorTemp, relevanceTemp, imageURLTemp, defaultImage, difficultyTemp, timeTemp, ratingTemp);
					adapter.add(newRecipe);
				}
				catch(JSONException jse){
					jse.printStackTrace();
				}
			}

			loadingDialog.dismiss();

			//Download Recipe Image's
			for (int p=0; p<NumRecipes; p++) {
				if (hasImage[p]) {
					new DownloadImageTask(p).execute(adapter.getItem(p).imageURL);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public boolean isOnline() {
	    ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

	    if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
		    final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		    builder.setMessage("No Internet Connection Found! Connect to Internet?")
		           .setCancelable(true)
		           .setPositiveButton("No", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, final int id) {
		                    dialog.cancel();
		                    finish();
		               }
		           })
		           .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, final int id) {
		            	   try {
		                   startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		                   finish();
		            	   } catch (Exception e) {
		            		   e.printStackTrace();
		            	   }
		               		
		               }
		           });
		    final AlertDialog alert = builder.create();
		    alert.show();
	        return false;
	    }
	return true; 
	}

	//Downloads Images From URL
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {		
		int ItemNum;

		public DownloadImageTask(int itemNum) {
			this.ItemNum = itemNum;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			adapter.getItem(ItemNum).Image = result;
			adapter.notifyDataSetChanged();
		}
	}
	
	private void showFilterDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_search_filter, null);
		
		final Spinner difficultyDropdown = (Spinner)view.findViewById(R.id.difficultyFilterDropDown);
		final Spinner difficultyValuesDropdown = (Spinner)view.findViewById(R.id.difficultyValuesDropDown);
		final Spinner cookTimeDropdown = (Spinner)view.findViewById(R.id.cooktimeFilterDropDown);
		final EditText cookTimeValue = (EditText)view.findViewById(R.id.cooktimeFilterValue);
		final Spinner ratingDropdown = (Spinner)view.findViewById(R.id.ratingFilterDropDown);
		final Spinner ratingValuesDropdown = (Spinner)view.findViewById(R.id.ratingValuesDropDown);
		
		final CheckBox difficultyCheckBox = (CheckBox)view.findViewById(R.id.checkBoxDifficulty);
		final CheckBox cookTimeCheckBox = (CheckBox)view.findViewById(R.id.checkBoxCookTime);
		final CheckBox ratingCheckBox = (CheckBox)view.findViewById(R.id.checkBoxRating);
		
		final LinearLayout difficultyFilters = (LinearLayout)view.findViewById(R.id.difficultyFilters);
		final LinearLayout cookTimeFilters = (LinearLayout)view.findViewById(R.id.cookTimeFilters);
		final LinearLayout ratingFilters = (LinearLayout)view.findViewById(R.id.ratingFilters);
		
		String[] operators = new String[]{"Greater", "Less"};
		String[] difficultyValues = new String[]{"Easy", "Intermediate", "Hard"};
		String[] ratingValues = new String[]{"1", "2", "3", "4", "5"};
		
		ArrayAdapter<String> operatorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
		ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, difficultyValues);
		ArrayAdapter<String> ratingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ratingValues);
		
		difficultyDropdown.setAdapter(operatorAdapter);
		difficultyValuesDropdown.setAdapter(difficultyAdapter);
		ratingDropdown.setAdapter(operatorAdapter);
		ratingValuesDropdown.setAdapter(ratingAdapter);
		cookTimeDropdown.setAdapter(operatorAdapter);
		
		difficultyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   if (isChecked){
	    				difficultyFilters.setVisibility(View.VISIBLE);
	    			} else {
	    				difficultyFilters.setVisibility(View.GONE);
	    			}
			   }
		});
		
		cookTimeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   if (isChecked){
					   cookTimeFilters.setVisibility(View.VISIBLE);
	    			} else {
	    				cookTimeFilters.setVisibility(View.GONE);
	    			}
			   }
		});
		
		ratingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   if (isChecked){
					   ratingFilters.setVisibility(View.VISIBLE);
	    			} else {
	    				ratingFilters.setVisibility(View.GONE);
	    			}
			   }
		});
		
		builder.setView(view)	
			.setTitle("Filter Results")
			.setPositiveButton(R.string.filter,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							boolean filterDifficulty = difficultyCheckBox.isChecked();
							boolean filterCookTime = cookTimeCheckBox.isChecked();
							boolean filterRating = ratingCheckBox.isChecked();
							
							if (filterDifficulty){
								int difficulty = getNumericalDifficulty((String) difficultyValuesDropdown.getSelectedItem());
								filterResults(difficultyDropdown.getSelectedItem().equals("Greater"), difficulty, "difficulty");
							}
							
							if (filterCookTime){
								int cookTime = Integer.parseInt(cookTimeValue.getText().toString());
								filterResults(cookTimeDropdown.getSelectedItem().equals("Greater"), cookTime, "cookTime");
							}
							
							if (filterRating){
								int rating = Integer.parseInt((String) ratingValuesDropdown.getSelectedItem());
								filterResults(ratingDropdown.getSelectedItem().equals("Greater"), rating, "rating");
							}
							
							adapter.notifyDataSetChanged();
						}
					})
			.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
						}
					});
		
		builder.create().show();
	}
	
	private void filterResults(boolean dropDownValue, int filterValue, String recipeComponent){
		
		Iterator<Recipe> recipeIterator = arrayOfRecipes.iterator();
		
		if (dropDownValue){
			
			while (recipeIterator.hasNext()){
				
				Recipe recipe = recipeIterator.next();
				
				if (recipeComponent.equals("difficulty")){
				
					if (getNumericalDifficulty(recipe.Difficulty) < filterValue){
						
						recipeIterator.remove();
					}
				} else if (recipeComponent.equals("cookTime")){
					
					if (recipe.Time < filterValue){
						
						recipeIterator.remove();
					}
				} else if (recipeComponent.equals("rating")){
					
					if (recipe.Rating < filterValue){
						
						recipeIterator.remove();
					}
				}
			}
			
		
		} else {
			
			while (recipeIterator.hasNext()){
				
				Recipe recipe = recipeIterator.next();
				
				if (recipeComponent.equals("difficulty")){
					if (getNumericalDifficulty(recipe.Difficulty) > filterValue){
						
						recipeIterator.remove();
					}
				} else if (recipeComponent.equals("cookTime")){
					
					if (recipe.Time > filterValue){
						
						recipeIterator.remove();
					}
				} else if (recipeComponent.equals("rating")){
					
					if (recipe.Rating > filterValue){
						
						recipeIterator.remove();
					}
				}
			}
		}
		
	}

	private int getNumericalDifficulty(String difficulty){
		
		int difficultyInt = 0;
		
		if (difficulty.equals("Hard")){
			difficultyInt = 3;
		} else if (difficulty.equals("Intermediate")){
			difficultyInt = 2;
		} else {
			difficultyInt = 1;
		}
		
		return difficultyInt;
	}
}
