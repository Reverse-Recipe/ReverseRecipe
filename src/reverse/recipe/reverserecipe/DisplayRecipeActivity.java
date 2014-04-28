package reverse.recipe.reverserecipe;

import org.json.JSONException;
import org.json.JSONObject;

import reverse.recipe.reverserecipe.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DisplayRecipeActivity extends Activity implements AsyncResponse {
	ProgressDialog loadingDialog;

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
		
		loadingDialog = new ProgressDialog(DisplayRecipeActivity.this);
		loadingDialog.setMessage("Retrieving Recipe");
		loadingDialog.show();
		
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
		
		fillRecipeLayout(recipe);
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
		
		recipeTitle.setText(recipe.getTitle());
		recipeAuthor.setText("Author: " + Integer.toString(recipe.getAuthorId()));
		recipeDifficulty.setText("Difficulty: " + recipe.getDifficulty());
		recipePrepTime.setText("Prep Time: " + Integer.toString(recipe.getPrepTime()));
		recipeCookTime.setText("Cook Time: " + Integer.toString(recipe.getCookTime()));
		recipeRating.setText("Rating: " + Integer.toString(recipe.getRating()));
		recipeYield.setText("Yield: " + recipe.getYield());
		ingredientList.setText(ingredientsFormattedText);
		methodList.setText(methodFormattedText);
		
		loadingDialog.dismiss();
	}
}
