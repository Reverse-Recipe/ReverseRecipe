package reverse.recipe.reverserecipe;

import java.io.InputStream;

import reverse.recipe.reverserecipe.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayRecipeActivity extends Activity implements AsyncResponse {
	ProgressDialog loadingDialog;
	RecipeDetails recipe = null;

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
	
	public void displayRecipeWithIntentExtras() {
		
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
		
		recipe = new RecipeDetails(recipeTitle, recipeId, recipeAuthor, recipeImageURL, recipeDifficulty, recipeCookTime, recipePrepTime, recipeRating, recipeYield);
		
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
		recipeAuthor.setText("Author: " + Integer.toString(recipe.getAuthorId()));
		recipeDifficulty.setText("Difficulty: " + recipe.getDifficulty());
		recipePrepTime.setText("Prep Time: " + Integer.toString(recipe.getPrepTime()));
		recipeCookTime.setText("Cook Time: " + Integer.toString(recipe.getCookTime()));
		recipeRating.setText("Rating: " + Integer.toString(recipe.getRating()));
		recipeYield.setText("Yield: " + recipe.getYield());
		ingredientList.setText(ingredientsFormattedText);
		methodList.setText(methodFormattedText);
		
		// record that the recipe has been viewed by adding to the analytics database
		recordToDatabase(recipe);
		
		loadingDialog.dismiss();
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
			Drawable d = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(result, 140, 110, true));
			TextView recipeImage = (TextView)findViewById(R.id.recipeTitle);
			recipeImage.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		}
	}
	
}
