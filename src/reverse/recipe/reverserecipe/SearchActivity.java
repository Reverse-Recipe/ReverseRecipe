package reverse.recipe.reverserecipe;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
	boolean[] hasImage = new boolean[30]; //Stores which recipes have an image (to load later)
	Bitmap defaultImage;
	ProgressDialog loadingDialog;

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
		final ArrayList<Recipe> arrayOfRecipes = new ArrayList<Recipe>();
		String recipeSearchStr;

		searchIngredients = bundle.getStringArrayList("searchTerms"); //here
		
		DbHelper db = new DbHelper(this);
		
		for (int i = 0; i < searchIngredients.size(); i++){
			db.addIngredient(searchIngredients.get(i));
			db.addToIngredientCount(searchIngredients.get(i));
		}
		
		adapter = new recipeArrayAdapter(this, arrayOfRecipes); //to here
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
		Bitmap Image;
		String imageURL;

		public Recipe(String titleT, int idT, String authorT, double relevanceT, String imageURLT, Bitmap imageT) {
			this.Title = titleT;
			this.ID = idT;
			this.Author = authorT;

			DecimalFormat dec = new DecimalFormat("0.00");
			this.Relevance = Double.parseDouble(dec.format(relevanceT)); //Round to 2 decimals

			this.imageURL = imageURLT;
			this.Image = imageT;
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
					String imageURLTemp = recipeObject.getString("image").replace("\\/", "/");

					//Mark Recipe's That Need Their Image Downloaded
					if (!"NULL".equals(imageURLTemp)) {
						hasImage[p-1] = true;
					} else {
						hasImage[p-1] = false;
					}

					//Display Recipe In List
					Recipe newRecipe = new Recipe(titleTemp, idTemp, authorTemp, relevanceTemp, imageURLTemp, defaultImage);
					adapter.add(newRecipe);
				}
				catch(JSONException jse){
					jse.printStackTrace();
				}
			}

			loadingDialog.dismiss();

			//Download Recipe Image's
			for (int p=0; p<30; p++) {
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
}
