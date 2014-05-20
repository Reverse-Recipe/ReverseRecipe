package reverse.recipe.reverserecipe;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CookBookRecipeActivity extends Activity {

	RecipeDetails recipe = null;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cookbook_recipe);

		prefs = getSharedPreferences("reverseRecipe", Context.MODE_PRIVATE);

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
		String[] recipeMethod = bundle.getStringArray("recipeMethod");
		String[] recipeIngredients = bundle.getStringArray("recipeIngredients");

		recipe = new RecipeDetails(recipeTitle, recipeId, recipeAuthor, recipeImageURL, recipeDifficulty, recipeCookTime, recipePrepTime, recipeRating, recipeYield, 0);
		recipe.setMethod(recipeMethod);
		recipe.setIngredients(recipeIngredients);

		if (!"NULL".equals(recipeImageURL) && isOnline()) {
			new DownloadImageTask().execute(recipeImageURL);
		}

		setViews();

	}

	public void setViews() {

		TextView recipeTitle = (TextView) findViewById(R.id.recipeTitle);
		TextView recipeAuthor = (TextView) findViewById(R.id.recipeAuthor);
		TextView recipeDifficulty = (TextView) findViewById(R.id.recipeDifficulty);
		TextView recipePrepTime = (TextView) findViewById(R.id.recipePrepTime);
		TextView recipeCookTime = (TextView) findViewById(R.id.recipeCookTime);
		TextView recipeRating = (TextView) findViewById(R.id.recipeRating);
		TextView recipeYield = (TextView) findViewById(R.id.recipeYield);
		TextView ingredientList = (TextView) findViewById(R.id.ingredientList);
		TextView methodList = (TextView) findViewById(R.id.methodList);

		recipeTitle.setText(recipe.getTitle(), TextView.BufferType.SPANNABLE);
		recipeAuthor.setText("Author: " + recipe.getAuthor());
		recipeDifficulty.setText("Difficulty: " + recipe.getDifficulty());
		recipePrepTime.setText("Prep Time: " + Integer.toString(recipe.getPrepTime()));
		recipeCookTime.setText("Cook Time: " + Integer.toString(recipe.getCookTime()));
		recipeRating.setText("Rating: " + Integer.toString(recipe.getRating()));
		recipeYield.setText("Yield: " + recipe.getYield());

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

		ingredientList.setText(ingredientsFormattedText);
		methodList.setText(methodFormattedText);
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

	public boolean isOnline() {
		ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

		if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
			return false;
		}
		return true; 
	}
	
	public void deleteRecipe(View view) {
		
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
		
		Button saveCookBook = (Button)findViewById(R.id.saveCookBook);
		if (position != -1) {
			saveCookBook.setText("Deleted");
		}
				
		prefs.edit().putString("reverseRecipe.savedCookBook", newList.toString()).commit(); 
		
		Intent intent = new Intent(CookBookRecipeActivity.this, CookBookActivity.class);
		startActivity(intent);
	}

}
