package reverse.recipe.reverserecipe;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import reverse.recipe.reverserecipe.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayRecipeActivity extends Activity implements AsyncResponse {
	ProgressDialog loadingDialog;
	RecipeDetails recipe = null;
	SharedPreferences prefs;
	String recipeInfo;
	CountDownTimer countDown;
	String pausedAt;
	Button pauseButton;
	Button stopButton;
	LinearLayout timerInput;
	LinearLayout timerDisplay;
	TextView timerLabel;
	TextView timerInputText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_recipe);
		setupActionBar();

		if (getIntent().hasExtra("recipeId")){
			if (isOnline()) {
				displayRecipeWithIntentExtras();	
			}
		}
	}

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

	public void displayRecipeWithIntentExtras() {

		prefs = getSharedPreferences("reverseRecipe", Context.MODE_PRIVATE);

		loadingDialog = new ProgressDialog(DisplayRecipeActivity.this);
		loadingDialog.setMessage("Retrieving Recipe");
		loadingDialog.show();

		Bundle bundle = getIntent().getExtras();
		String recipeId = bundle.getString("recipeId");
		int recipeRating = bundle.getInt("recipeRating");
		String recipeAuthor = bundle.getString("recipeAuthor");
		String recipeTitle = bundle.getString("recipeTitle");
		String recipeDifficulty = bundle.getString("recipeDifficulty");
		String recipeYield = bundle.getString("recipeYield");
		int recipeCookTime = bundle.getInt("recipeCookTime");
		int recipePrepTime = bundle.getInt("recipePrepTime");
		String recipeImageURL = bundle.getString("recipeImageURL");		

		recipe = new RecipeDetails(recipeTitle, recipeId, recipeAuthor, recipeImageURL, recipeDifficulty, recipeCookTime, recipePrepTime, recipeRating, recipeYield, 0);

		if (checkIfSaved() != -1) {
			Button saveCookBook = (Button)findViewById(R.id.saveCookBook);
			saveCookBook.setText("Saved");
		}
		
		new GetSingleRecipe(this).execute(recipeId); //get recipe data
		new DownloadImageTask().execute(recipeImageURL);
	}

	//Method from AsyncResponse interface
	@Override
	public void responseObtained(String output) {

		//create recipe object
		try{

			String[] recipeSections = output.split(",\"split here\":\"\",");
			String recipeIngredients = "";
			String recipeMethod = "";
			recipeInfo = output;

			if (recipeSections.length >= 1) {
				recipeIngredients = recipeSections[0] + "}";
				recipeMethod = "{" + recipeSections[1];
				//String recipeNutrition = recipeSections[3];
			}

			//get the recipe ingredients and method
			recipe.setIngredients(Utilities.iterateThroughJson(recipeIngredients, "Ingredients", "number of ingredients"));
			recipe.setMethod(Utilities.iterateThroughJson(recipeMethod, "steps", "number of steps"));
			//recipe.setNutritionInfo(Utilities.iterateThroughJson(recipeNutrition, "nutrition labels", "number of labels"));

			fillRecipeLayout(recipe);
		} catch(Exception e){
		}
		loadingDialog.dismiss();
	}

	public void fillRecipeLayout(RecipeDetails recipe){
		TextView recipeTitle = (TextView) findViewById(R.id.recipeTitle);
		TextView recipeAuthor = (TextView) findViewById(R.id.recipeAuthor);
		TextView recipeDifficulty = (TextView) findViewById(R.id.recipeDifficulty);
		TextView recipePrepTime = (TextView) findViewById(R.id.recipePrepTime);
		TextView recipeCookTime = (TextView) findViewById(R.id.recipeCookTime);
		TextView recipeRating = (TextView) findViewById(R.id.recipeRating);
		TextView recipeYield = (TextView) findViewById(R.id.recipeYield);
		TextView ingredientList = (TextView) findViewById(R.id.ingredientList);
		TextView methodList = (TextView) findViewById(R.id.methodList);

		String[] ingredients = recipe.getIngredients();
		String[] method = recipe.getMethod();
		String ingredientsFormattedText = "";
		String methodFormattedText = "";

		for (int i = 0; i < ingredients.length; i++){
			ingredientsFormattedText += ingredients[i] + "\n";
		}

		for (int i = 0; i < method.length; i++){
			methodFormattedText += Integer.toString(i + 1) + ". " + method[i] + "\n\n";
		}

		recipeTitle.setText(recipe.getTitle(), TextView.BufferType.SPANNABLE);
		recipeAuthor.setText("Author: " + recipe.getAuthor());
		recipeDifficulty.setText("Difficulty: " + recipe.getDifficulty());
		recipePrepTime.setText("Prep Time: " + Integer.toString(recipe.getPrepTime()));
		recipeCookTime.setText("Cook Time: " + Integer.toString(recipe.getCookTime()));
		recipeRating.setText("Rating: " + Integer.toString(recipe.getRating()));
		recipeYield.setText("Yield: " + recipe.getYield());
		ingredientList.setText(ingredientsFormattedText);
		methodList.setText(methodFormattedText);

		// record that the recipe has been viewed by adding to the analytics database
		recordToDatabase(recipe);
	}

	private void recordToDatabase(RecipeDetails recipe) {
		DbHelper db = new DbHelper(this);
		String recipeName = recipe.getTitle();
		String difficulty = recipe.getDifficulty();
		int prepTime = recipe.getPrepTime();
		int cookTime = recipe.getCookTime();
		db.addRecipe(recipeName, difficulty, prepTime, cookTime);
		db.addToRecipeCount(recipeName);
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
			try {
				Drawable d = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(result, 140, 110, true));
				TextView recipeImage = (TextView)findViewById(R.id.recipeTitle);
				recipeImage.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
			} catch (Exception e) {

			}
		}
	}
	
	public int checkIfSaved() {
		JSONArray jsonArray;
		String recipeJSON = prefs.getString("reverseRecipe.savedCookBook", "None Found");
		if (recipeJSON != "None Found") {
			try {
				jsonArray = new JSONArray(recipeJSON);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				jsonArray = new JSONArray();
				e.printStackTrace();
			}
		} else {
			jsonArray = new JSONArray();
		}

		int position = -1;
		for (int x = 0; x < jsonArray.length(); x++) {
			try {
				if (recipe.getId().equals(jsonArray.getJSONObject(x).getString("Id"))) {
					position = x;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return position;
	}

	public void saveCookBook(View view) {

		JSONArray jsonArray;
		String recipeJSON = prefs.getString("reverseRecipe.savedCookBook", "None Found");
		if (recipeJSON != "None Found") {
			try {
				jsonArray = new JSONArray(recipeJSON);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				jsonArray = new JSONArray();
				e.printStackTrace();
			}
		} else {
			jsonArray = new JSONArray();
		}
		
		int position = checkIfSaved();
		
		Button saveCookBook = (Button)findViewById(R.id.saveCookBook);
		if (position == -1) {
			jsonArray.put(getJSONObject());
			saveCookBook.setText("Saved");
		} else {
			saveCookBook.setText("Save To Cook Book");	
		}
					
		//Deletes Recipe If Deselected
		JSONArray newList = new JSONArray();     
		int len = jsonArray.length();
		for (int i=0;i<len;i++)
		{ 
			//Excluding the item at position
			if (i != position) 
			{
				try {
					newList.put(jsonArray.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 

		prefs.edit().putString("reverseRecipe.savedCookBook", newList.toString()).commit(); 
	}

	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Id", recipe.getId());
			obj.put("Title", recipe.getTitle());
			obj.put("Author", recipe.getAuthor());
			obj.put("CookTime", recipe.getCookTime());
			obj.put("PrepTime", recipe.getPrepTime());
			obj.put("Difficulty", recipe.getDifficulty());
			obj.put("ImageURL", recipe.getImageUrl());
			obj.put("Rating", recipe.getRating());
			obj.put("Yield", recipe.getYield());

			/* Should be done better, cant use JSONArray so not sure what to do, at the moment
			 * am distinguishing lines of method/ingredients by a random string " @24! "
			 */
			String methodString = "";
			for (int x = 0; x < recipe.getMethod().length; x++) {
				methodString += recipe.getMethod()[x] + " @24! ";
			}
			obj.put("Method", methodString);

			String ingredientString = "";
			for (int x = 0; x < recipe.getIngredients().length; x++) {
				ingredientString += recipe.getIngredients()[x] + " @24! ";
			}
			obj.put("Method", methodString);
			obj.put("Ingredients", ingredientString);

		} catch (JSONException e) {
		}
		return obj;
	}
	
	public void startTimer(View view) {
		
		timerInputText = (TextView) findViewById(R.id.timerInputText);
		timerLabel = (TextView)findViewById(R.id.timerLabel);
		Long startTime = Long.valueOf(timerInputText.getText().toString()) * 60000;
		timerInput = (LinearLayout)findViewById(R.id.timerInput);
		timerDisplay = (LinearLayout)findViewById(R.id.timerDisplay);
		timerInput.setVisibility(View.GONE);
		timerDisplay.setVisibility(View.VISIBLE);
		pauseButton = (Button)findViewById(R.id.pauseCooking);
		stopButton = (Button)findViewById(R.id.stopCooking);
		
		pauseButton.setOnClickListener(new Button.OnClickListener(){
    		
    		@Override
    	   public void onClick(View arg0) {
    			
    			if (pauseButton.getText().equals("Pause")){
	    			countDown.cancel();
	    			pausedAt = timerLabel.getText().toString();
	    			pauseButton.setText("Resume");
    			} else {
    				
    				String[] splitTimer = pausedAt.split(":");
    				long minutes = Long.parseLong(splitTimer[0]);
    				long seconds = Long.parseLong(splitTimer[1]);
    				long startTime = (minutes * 60 + seconds) * 1000;
    				
    				countDown = initiateTimer(startTime);	
    				countDown.start();
    				pauseButton.setText("Pause");
    			}
	   }});
		
		stopButton.setOnClickListener(new Button.OnClickListener(){
    		
    		@Override
    	   public void onClick(View arg0) {
    			countDown.cancel();
    			timerInput.setVisibility(View.VISIBLE);
    			timerDisplay.setVisibility(View.GONE);
    			timerInputText.setText("");
    			
	   }});
		
		countDown = initiateTimer(startTime);	
		countDown.start();

	}
	

	public CountDownTimer initiateTimer(long startTime){
		
		countDown = new CountDownTimer(startTime, 1000) {
			
			
			
			
			 public void onTick(long millisUntilFinished) {
				 
				 long minutes = millisUntilFinished / 1000 / 60;
				 long seconds = millisUntilFinished / 1000 % 60;
				 timerLabel = (TextView)findViewById(R.id.timerLabel);
				 
				 timerLabel.setText(String.format("%02d", minutes) + ":" + String.format("%02d",seconds));
			 }

			 public void onFinish() {
				 timerInput.setVisibility(View.VISIBLE);
				 timerDisplay.setVisibility(View.GONE);
				 timerInputText.setText("");
				 
				 AlertDialog.Builder builder = new AlertDialog.Builder(DisplayRecipeActivity.this);
					
				 builder.setTitle("Timer Finished!")
				 	.setMessage("Your timer has finished")
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});
				
				builder.create().show();
			 }
		};
		
		return countDown;
	}

}
