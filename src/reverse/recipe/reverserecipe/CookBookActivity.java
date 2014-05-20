package reverse.recipe.reverserecipe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class CookBookActivity extends ListActivity {

	SharedPreferences prefs;
	recipeArrayAdapter adapter;
	ArrayList<RecipeDetails> arrayOfRecipes;
	ArrayList<String> recipeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cookbook);
		setupActionBar();
		
		arrayOfRecipes = new ArrayList<RecipeDetails>();
		adapter = new recipeArrayAdapter(this, arrayOfRecipes);
		setListAdapter(adapter);
		

		//RecipeDetails newRecipe = new RecipeDetails(titleTemp, idTemp, authorTemp, imageURLTemp, difficultyTemp, cookTimeTemp, prepTimeTemp, ratingTemp, yieldTemp, relevanceTemp);
		
		//adapter.add(newRecipe);
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
	
	//Stores Recipes Found
		public class Recipe { 
			String Title;
			int ID;
			String Author;
			double Relevance;
			Bitmap Image;
			String imageURL;
			int Time;
			int cookTime;
			int prepTime;
			String Difficulty;
			int Rating;
			String Yield;

			public Recipe(String titleT, int idT, String authorT, double relevanceT, String imageURLT, Bitmap imageT, String difficultyT, int cookTimeT, int prepTimeT, int ratingT, String yieldT) {
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
				
				this.Time = cookTimeT + prepTimeT;
				this.cookTime = cookTimeT;
				this.prepTime = prepTimeT;
				
				this.imageURL = imageURLT;
				this.Image = imageT;
				
				this.Rating = ratingT;
				this.Yield = yieldT;
			}
		}
}
