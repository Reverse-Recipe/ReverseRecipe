package reverse.recipe.reverserecipe;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DisplayRecipeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_recipe);
		setupActionBar();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.display_recipe, menu);
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
	
	public void displayRecipe(View view) {
		
		String recipeId = "16583"; //random id for now
		
		new GetRecipe().execute(recipeId);
	}
	
	private class GetRecipe extends AsyncTask<String, Void, String>{
		
		protected String doInBackground(String... id) {
			
			String recipeURL = "http://www.reverserecipe.host22.com/api/?tag=getRecipe&id=" + id[0];
			String ingredientsURL = "http://www.reverserecipe.host22.com/api/?tag=getRecipeIngredients&id=" + id[0];
			String methodURL = "http://www.reverserecipe.host22.com/api/?tag=getRecipeMethod&id=" + id[0];
			String nutritionURL = "http://www.reverserecipe.host22.com/api/?tag=getNutritionInfo&id=" + id[0];

			return Utilities.fetchData(recipeURL).toString() + 
					Utilities.fetchData(ingredientsURL).toString() +
					Utilities.fetchData(methodURL).toString();
					//Utilities.fetchData(nutritionURL).toString();	 Not working atm, doesn't like when there is 0 values
		}
		
		protected void onPostExecute(String info) {
			super.onPostExecute(info);
			
			String[] recipeSections = info.split("<!-- End Of Analytics Code -->");
			String recipeInfo = recipeSections[0];
			String recipeIngredients = recipeSections[1];
			String recipeMethod = recipeSections[2];
			//String recipeNutrition = recipeSections[3];
			RecipeDetails recipe = null;
			
			try{

				JSONObject recipeObject = new JSONObject(recipeInfo);
				
				int id = recipeObject.getInt("recipe id");
				String title = recipeObject.getString("title");
				int prepTime = recipeObject.getInt("prep time");
				int inactiveTime = recipeObject.getInt("inactive time");
				int cookTime = recipeObject.getInt("cook time");
				String difficulty = recipeObject.getString("difficulty");
				int rating  = recipeObject.getInt("rating");
				String yield = recipeObject.getString("yield");
				int authorId = recipeObject.getInt("author");
				String imageUrl = recipeObject.getString("image");
				String url = recipeObject.getString("url");
				
				recipe = new RecipeDetails(id, title, prepTime, inactiveTime, cookTime, difficulty, rating, yield, authorId, imageUrl, url);
				recipe.getIngredients();
				
			} catch(JSONException jse){
				jse.printStackTrace();
			}
			
			recipe.setIngredients(Utilities.iterateThroughJson(recipeIngredients, "Ingredients", "number of ingredients"));
			recipe.setMethod(Utilities.iterateThroughJson(recipeMethod, "steps", "number of steps"));
			//recipe.setNutritionInfo(Utilities.iterateThroughJson(recipeNutrition, "nutrition labels", "number of labels"));
			
			//lets see if it worked
			
			Log.v("", recipe.getTitle());
			Log.v("", recipe.getIngredients()[0]);
			Log.v("", recipe.getMethod()[0]);
			//Log.v("", String.valueOf(recipe.getNutritionInfo().length));
			
		}		
	}
}
