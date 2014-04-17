package reverse.recipe.reverserecipe;

import android.os.AsyncTask;

//gets all the ingredients in database and passes them back to calling activity
public class GetAllIngredients extends AsyncTask<String, Void, String>{
	
	private AsyncResponse listener;
	
	public GetAllIngredients(AsyncResponse listener){
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(String... id) {
		
		String ingredientsURL = "http://www.reverserecipe.host22.com/api/?tag=getAllIngredients";

		return Utilities.fetchData(ingredientsURL).toString();
	}
	
	@Override
	protected void onPostExecute(String info) {
		listener.responseObtained(info);
	}
}