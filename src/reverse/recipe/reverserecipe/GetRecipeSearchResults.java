package reverse.recipe.reverserecipe;

import android.os.AsyncTask;

//gets the search results and passes them back to calling activity
public class GetRecipeSearchResults extends AsyncTask<String, Void, String> {

	private AsyncResponse listener;
	
	public GetRecipeSearchResults(AsyncResponse listener){
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(String... recipeURL) {
		
		String recipeSearchURL = recipeURL[0];
			
		return Utilities.fetchData(recipeSearchURL).toString();

	}

	@Override
	protected void onPostExecute(String info) {
		listener.responseObtained(info);
	}

}