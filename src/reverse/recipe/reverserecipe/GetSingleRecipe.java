package reverse.recipe.reverserecipe;

import android.os.AsyncTask;

//gets a specific recipes details and passes them back to calling activity
public class GetSingleRecipe extends AsyncTask<String, Void, String>{
	
	private AsyncResponse listener;
	
	public GetSingleRecipe(AsyncResponse listener){
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(String... id) {
		
		String recipeURL = "http://www.reverserecipe.host22.com/api/?tag=getRecipe&id=" + id[0];
		String ingredientsURL = "http://www.reverserecipe.host22.com/api/?tag=getRecipeIngredients&id=" + id[0];
		String methodURL = "http://www.reverserecipe.host22.com/api/?tag=getRecipeMethod&id=" + id[0];
		//String nutritionURL = "http://www.reverserecipe.host22.com/api/?tag=getNutritionInfo&id=" + id[0];

		return Utilities.fetchData(recipeURL).toString() + 
				Utilities.fetchData(ingredientsURL).toString() +
				Utilities.fetchData(methodURL).toString();
				//Utilities.fetchData(nutritionURL).toString();	 Not working atm, doesn't like when there is 0 values
	}
	
	@Override
	protected void onPostExecute(String info) {		
		listener.responseObtained(info);
	}		
}
