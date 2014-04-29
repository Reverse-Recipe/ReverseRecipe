package reverse.recipe.reverserecipe;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;

import com.google.gson.Gson;

import reverse.recipe.reverserecipe.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MyPantryActivity extends Fragment implements AsyncResponse {
	Button addButton;
	Button deleteButton;
	Button searchButton;
	EditText editText;
	ArrayList<String> pantryList;
	String savedPantry;
	ListView listView;
	ArrayAdapter<String> adapter;
	String[] allIngredients;
	SharedPreferences prefs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_my_pantry, container, false);
		
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		//gets items already saved
		prefs = getView().getContext().getSharedPreferences("reverseRecipe", Context.MODE_PRIVATE);
		savedPantry = prefs.getString("reverseRecipe.savedPantry", "Didnt work");
		pantryList = new ArrayList<String>();
		
	    try {
			pantryList = Utilities.jsonStringToArray(savedPantry);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	    new GetAllIngredients(this).execute(); //populate auto complete
	    
		// declare xml elements
	    addButton = (Button)getView().findViewById(R.id.addIngredientButton);
		deleteButton = (Button)getView().findViewById(R.id.deleteButton);
	    editText = (EditText)getView().findViewById(R.id.ingredientsSearch);
	    listView = (ListView)getView().findViewById(R.id.ingredientList);
	    searchButton = (Button)getView().findViewById(R.id.searchButton);
	    
	    //puts saved pantry ingredients into list view
	    adapter = new ArrayAdapter<String>(getView().getContext(), android.R.layout.simple_list_item_multiple_choice, pantryList);   
	    listView.setAdapter(adapter);
	    
	    addButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				String input = editText.getText().toString();
				
				//adds ingredient to pantry list if it exists in db and not already on list
				//saves pantry list to phone
				if (Arrays.asList(allIngredients).contains(input) && !(pantryList.contains(input))) {
			        adapter.add(input);	        
			        String pantryObject = new Gson().toJson(pantryList);       
			        prefs.edit().putString("reverseRecipe.savedPantry", pantryObject).commit(); 
			        adapter.notifyDataSetChanged();
			        editText.setText("");
			    }				
			}   	
	    });
	    
	    deleteButton.setOnClickListener(new View.OnClickListener(){

	    	//delete selected items from list and updates pantry list saved on phone
	    	@Override
			public void onClick(View arg0) {
				
                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
 
                for(int i = listView.getCount() - 1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                        adapter.remove(pantryList.get(i));
                    }
                }
                
                checkedItemPositions.clear();
                String pantryObject = new Gson().toJson(pantryList);       
		        prefs.edit().putString("reverseRecipe.savedPantry", pantryObject).commit(); 
		        adapter.notifyDataSetChanged();
			}    	
	    });
	    
	    searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GoToSearchRecipes();
			}
		});
	}
	
	//goes to search activity and starts a search with selected pantry ingredients
	private void GoToSearchRecipes() {
		
		//bundle stores the selected ingredients
		Intent intent_recipes = new Intent(getView().getContext(),SearchActivity.class);
		Bundle bundle = new Bundle();
		ArrayList<String> ingredientsSelected = new ArrayList<String>();
		
		SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
		 
        //get all selected ingredients
		for (int i = listView.getCount() - 1; i >= 0; i--){
            if (checkedItemPositions.get(i)){
                ingredientsSelected.add(pantryList.get(i));
            }
        }
        
        checkedItemPositions.clear();
		
		//add selected ingredients to bundle and start activity
        bundle.putStringArrayList("searchTerms", ingredientsSelected);
		intent_recipes.putExtras(bundle);
		startActivity(intent_recipes);
	}

	@Override
	public void responseObtained(String output) {
		// TODO Auto-generated method stub
		
		try {
		AutoCompleteTextView autoComplete;
		ArrayAdapter<String> adapter;
		
		allIngredients = Utilities.iterateThroughJson(output,"ingredients", "number of ingredients");
		
		adapter = new ArrayAdapter<String>(getView().getContext(),android.R.layout.simple_list_item_1, allIngredients);
		
		//add all ingredients to auto complete
		autoComplete = (AutoCompleteTextView) getView().findViewById(R.id.ingredientsSearch);	
		autoComplete.setAdapter(adapter);
		autoComplete.setThreshold(2); //2 characters before suggesting
		} catch (Exception e) {
			
		}
	}

}
