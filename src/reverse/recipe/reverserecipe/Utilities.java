package reverse.recipe.reverserecipe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utilities {
	
	//returns JSON information from given URL
	public static StringBuilder fetchData(String URL){
		
		StringBuilder builder = new StringBuilder();	
		HttpClient client = new DefaultHttpClient();	
		
		try {
			//try to fetch the data
			HttpGet ingredientsGet = new HttpGet(URL);
			HttpResponse recipeResponse = client.execute(ingredientsGet); //Grabs Response

			StatusLine recipeSearchStatus = recipeResponse.getStatusLine(); //Checks If Anything Went Wrong In Search

			if (recipeSearchStatus.getStatusCode() == 200) {						//we have an OK response

				HttpEntity recipeEntity = recipeResponse.getEntity(); //Entity Holding Response
				InputStream recipeContent = recipeEntity.getContent(); //Retrieve JSON String
				InputStreamReader recipeInput = new InputStreamReader(recipeContent); //Create Read For JSON String
				BufferedReader recipeReader = new BufferedReader(recipeInput); //Carry Out String Reading

				//Reads 1 Line At A Time To Create String Of All data
				String lineIn;
				while ((lineIn = recipeReader.readLine()) != null) {
					builder.append(lineIn);
				}
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return builder;
	}
	
	//iterates through numbered 2D JSON object and returns in ordered array
	public static String[] iterateThroughJson(String jsonObject, String objectName, String sizeName){
		
		String [] values = null;
		
		try {
			JSONObject infoObject = new JSONObject(jsonObject);
			JSONObject object = infoObject.getJSONObject(objectName);
		
			int numValues = infoObject.getInt(sizeName);
			values = new String[numValues]; 

			for (int i = 0; i < numValues; i++) {

				try{
					values[i] = object.getString(String.valueOf(i +1)); 
				}
				catch(JSONException jse){
					jse.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return values;
	}
	
	//converts JSON string (not object) to an ArrayList
	public static ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {

	    ArrayList<String> stringArray = new ArrayList<String>();

	    JSONArray jsonArray = new JSONArray(jsonString);

	    for (int i = 0; i < jsonArray.length(); i++) {
	        stringArray.add(jsonArray.getString(i));
	    }

	    return stringArray;
	}
	
	//Creates search string and it encodes it from an array list of ingredients
	public static String arrayToSearchString(ArrayList<String> ingredients) throws UnsupportedEncodingException{
		
		String searchTerm = "";
		
		for (int i = 0; i < ingredients.size(); i++){
			searchTerm += URLEncoder.encode("\"" + ingredients.get(i) + "\"*", "UTF-8");
		}
		
		return searchTerm;
	}
}
