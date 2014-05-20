package reverse.recipe.reverserecipe;

import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CookBookActivity extends ListFragment {

	SharedPreferences prefs;
	recipeArrayAdapter adapter;
	ArrayList<RecipeDetails> arrayOfRecipes;
	ArrayList<String> recipeList;
	Bitmap defaultImage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_cookbook, container, false);

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		String recipeJSON = prefs.getString("reverseRecipe.savedCookBook", "None Found");
		ArrayList<String> recipes;
		try {
			recipes = Utilities.jsonStringToArray(recipeJSON);

			if (adapter.getCount() != recipes.size()) {
				initialOpen();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initialOpen();
	}

	public void initialOpen() {
		arrayOfRecipes = new ArrayList<RecipeDetails>();
		adapter = new recipeArrayAdapter(getView().getContext(), arrayOfRecipes);
		setListAdapter(adapter);

		prefs = getView().getContext().getSharedPreferences("reverseRecipe", Context.MODE_PRIVATE);
		String recipeJSON = prefs.getString("reverseRecipe.savedCookBook", "None Found");

		defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.food_network_logo);

		try {
			ArrayList<String> recipes = Utilities.jsonStringToArray(recipeJSON);

			for (int x = 0; x < recipes.size(); x++) {
				JSONObject resultObject = new JSONObject(recipes.get(x)); //Puts String Retrieved In JSONObject
				String Title = resultObject.getString("Title");
				String Author = resultObject.getString("Author");
				String Difficulty = resultObject.getString("Difficulty");
				String Id = resultObject.getString("Id");
				String Yield = resultObject.getString("Yield");
				int PrepTime = resultObject.getInt("PrepTime");
				int CookTime = resultObject.getInt("CookTime");
				int Rating = resultObject.getInt("Rating");
				String ImageURL = resultObject.getString("ImageURL");

				String[] Ingredients = resultObject.getString("Ingredients").split(" @24! ");
				String[] Method = resultObject.getString("Method").split(" @24! ");

				RecipeDetails newRecipe = new RecipeDetails(Title, Id, Author, ImageURL, Difficulty, CookTime, PrepTime, Rating, Yield, 0);
				newRecipe.setIngredients(Ingredients);
				newRecipe.setMethod(Method);
				newRecipe.setImage(defaultImage);

				adapter.add(newRecipe);

				if (!"NULL".equals(ImageURL) && isOnline()) {
					new DownloadImageTask(x).execute(adapter.getItem(x).getImageUrl());
				}

			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ListView list = getListView();
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

				Intent intent = new Intent(getView().getContext(), CookBookRecipeActivity.class);
				Bundle bundle = new Bundle();

				bundle.putString("recipeId", String.valueOf(arrayOfRecipes.get(pos).getId()));
				bundle.putInt("recipeRating", arrayOfRecipes.get(pos).getRating());
				bundle.putString("recipeAuthor", String.valueOf(arrayOfRecipes.get(pos).getAuthor()));
				bundle.putString("recipeTitle", String.valueOf(arrayOfRecipes.get(pos).getTitle()));
				bundle.putString("recipeDifficulty", String.valueOf(arrayOfRecipes.get(pos).getDifficulty()));
				bundle.putString("recipeYield", String.valueOf(arrayOfRecipes.get(pos).getYield()));
				bundle.putInt("recipeCookTime", arrayOfRecipes.get(pos).getCookTime());
				bundle.putInt("recipePrepTime", arrayOfRecipes.get(pos).getPrepTime());
				bundle.putString("recipeImageURL", String.valueOf(arrayOfRecipes.get(pos).getImageUrl()));
				bundle.putStringArray("recipeMethod", arrayOfRecipes.get(pos).getMethod());
				bundle.putStringArray("recipeIngredients", arrayOfRecipes.get(pos).getIngredients());

				intent.putExtras(bundle);
				startActivity(intent);
			}});
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
			adapter.getItem(ItemNum).setImage(result);
			adapter.notifyDataSetChanged();
		}
	}

	public boolean isOnline() {
		ConnectivityManager conMgr = (ConnectivityManager) getView().getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

		if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
			return false;
		}
		return true; 
	}
}
