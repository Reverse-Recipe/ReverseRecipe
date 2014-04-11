package reverse.recipe.reverserecipe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

public class SearchActivity extends ListActivity {
	recipeArrayAdapter adapter; //Adapter to display results
	boolean[] hasImage = new boolean[30]; //Stores which recipes have an image (to load later)
	Bitmap defaultImage; //Default Image For Recipe (Assigned to On Button Click)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		setupActionBar();
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

		// Create List Adapter for Results
		ArrayList<Recipe> arrayOfRecipes = new ArrayList<Recipe>();
		// Create the adapter to convert the array to views
		adapter = new recipeArrayAdapter(this, arrayOfRecipes);
		// Attach the adapter to a ListView
		setListAdapter(adapter);
		// Assign Default Image for Recipes
		defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

		String recipeSearchStr;
		recipeSearchStr = "http://www.reverserecipe.host22.com/api/?tag=searchRecipes&ingreds="; //Default Web Address
		recipeSearchStr += "cheese%2A+bacon"; //Add Ingredients Here
		new GetRecipes().execute(recipeSearchStr); //Execute Search
	}


	//ASyncTask To Work In Background (Parameters: Input Type, Progress Type (not used), Output Type)
	private class GetRecipes extends AsyncTask<String, Void, String> {

		//Grabs Data In Background		
		protected String doInBackground(String... recipeURL) {
			String recipeSearchURL = recipeURL[0];
			StringBuilder recipeBuilder = new StringBuilder();

			HttpClient recipeClient = new DefaultHttpClient(); //Web Client To Search

			try {
				//try to fetch the data
				HttpGet recipeGet = new HttpGet(recipeSearchURL);
				HttpResponse recipeResponse = recipeClient.execute(recipeGet); //Grabs Response

				StatusLine recipeSearchStatus = recipeResponse.getStatusLine(); //Checks If Anything Went Wrong In Search


				if (recipeSearchStatus.getStatusCode() == 200) {						//we have an OK response

					HttpEntity recipeEntity = recipeResponse.getEntity(); //Entity Holding Response
					InputStream recipeContent = recipeEntity.getContent(); //Retrieve JSON String
					InputStreamReader recipeInput = new InputStreamReader(recipeContent); //Create Read For JSON String
					BufferedReader recipeReader = new BufferedReader(recipeInput); //Carry Out String Reading

					//Reads 1 Line At A Time To Create String Of All Recipes
					String lineIn;
					while ((lineIn = recipeReader.readLine()) != null) {
						recipeBuilder.append(lineIn);
					}
				}

			}
			catch(Exception e){
				e.printStackTrace();
			}


			//Send Results To Next Stage
			String results = recipeBuilder.toString();			
			return results;

		}

		//Display Information
		protected void onPostExecute(String info) {
			super.onPostExecute(info);

			try {
				//parse JSON
				JSONObject resultObject = new JSONObject(info); //Puts String Retrieved In JSONObject
				JSONObject resultObject2 = resultObject.getJSONObject("recipes"); //Refines Object To Recipes

				int NumRecipes = resultObject.getInt("number of recipes"); // Grabs number of recipes

				//loop through recipes
				for (int p=1; p<=NumRecipes; p++) {

					try{
						//attempt to retrieve place data values
						JSONObject recipeObject = resultObject2.getJSONObject(String.valueOf(p)); //Get first object from array

						//Get Information From JSON
						String titleTemp = recipeObject.getString("title"); //Get Recipe Title
						int idTemp = recipeObject.getInt("recipe id");
						String authorTemp = recipeObject.getString("author");
						double relevanceTemp = recipeObject.getDouble("relevance");
						String imageURL = recipeObject.getString("image").replace("\\/", "/");

						//Mark Recipe's That Need Their Image Downloaded
						if (!"NULL".equals(imageURL)) {
							hasImage[p-1] = true;
						} else {
							hasImage[p-1] = false;
						}
						
						//Add Recipe Information
						Recipe newRecipe = new Recipe(titleTemp, idTemp, authorTemp, relevanceTemp, imageURL, defaultImage);
						adapter.add(newRecipe);
					}
					catch(JSONException jse){
						jse.printStackTrace();
					}
				}
				
				//Download Recipe Image's
				for (int p=0; p<30; p++) {
					if (hasImage[p]) {
						new DownloadImageTask(p).execute(adapter.getItem(p).imageURLT);
					}
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}

	} //END ASYNC


	//Stores Recipes Found
	public class Recipe { 
		String Title;
		int ID;
		String Author;
		double Relevance;
		Bitmap Image;
		String imageURLT;

		public Recipe(String titleT, int idT, String authorT, double relevanceT, String imageURLT, Bitmap imageT) {
			this.Title = titleT;
			this.ID = idT;
			this.Author = authorT;

			DecimalFormat dec = new DecimalFormat("0.00");
			this.Relevance = Double.parseDouble(dec.format(relevanceT)); //Round to 2 decimals

			this.imageURLT = imageURLT;
			this.Image = imageT;
		}
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
	            Log.e("Error", e.getMessage());
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
