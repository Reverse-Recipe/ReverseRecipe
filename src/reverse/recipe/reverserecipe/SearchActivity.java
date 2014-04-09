package reverse.recipe.reverserecipe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class SearchActivity extends Activity {

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

			//Create Recipe Information Variables
			String[] recipeTitle = new String[30];
			int[] recipeID = new int[30];
			String[] recipeAuthor = new String[30];
			double[] recipeRelevance = new double[30];
			
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
						
						recipeTitle[p-1] = recipeObject.getString("title"); //Get Recipe Title
						recipeID[p-1] = recipeObject.getInt("recipe id");
						recipeAuthor[p-1] = recipeObject.getString("author");
						recipeRelevance[p-1] = recipeObject.getDouble("relevance");
					}
					catch(JSONException jse){
						jse.printStackTrace();
					}
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			DisplayResults(recipeTitle, recipeID, recipeAuthor, recipeRelevance); //Method To Display Results
			
		}

	} //END ASYNC

	
	private void DisplayResults(String[] Titles, int[] IDs, String[] Authors, double[] Relevances) {
		
		//TEST Displaying Of First Result
		TextView resultsText = (TextView)findViewById(R.id.recipeResults);
		resultsText.setText("Title: " + Titles[0] + "  ID: " + IDs[0] + "  Author: " + Authors[0] + "  Relevance: " + Relevances[0]);
	}











}
