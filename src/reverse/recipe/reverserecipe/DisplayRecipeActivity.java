package reverse.recipe.reverserecipe;

import org.json.JSONException;
import org.json.JSONObject;
import reverse.recipe.reverserecipe.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DisplayRecipeActivity extends Activity implements AsyncResponse {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_recipe);
		setupActionBar();
		
		if (getIntent().hasExtra("recipeId")){
			displayRecipeWithIntentExtras();	
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
		
		new GetSingleRecipe(this).execute(recipeId); //get recipe data
	}
	
	public void displayRecipeWithIntentExtras() {
		
		Bundle bundle = getIntent().getExtras();
		String recipeId = bundle.getString("recipeId");
		
		new GetSingleRecipe(this).execute(recipeId); //get recipe data
	}

	//Method from AsyncResponse interface
	@Override
	public void responseObtained(String output) {
		
		String[] recipeSections = output.split("<!-- End Of Analytics Code -->");
		String recipeInfo = recipeSections[0];
		String recipeIngredients = recipeSections[1];
		String recipeMethod = recipeSections[2];
		//String recipeNutrition = recipeSections[3];
		RecipeDetails recipe = null;
		
		//create recipe object
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
			
			recipe = new RecipeDetails(id, title, prepTime, inactiveTime, cookTime, difficulty, rating, yield, authorId, imageUrl, url);;
			
		} catch(JSONException jse){
			jse.printStackTrace();
		}
		
		//get the recipe ingredients and method
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
