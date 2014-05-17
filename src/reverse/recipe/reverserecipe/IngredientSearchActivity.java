package reverse.recipe.reverserecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import reverse.recipe.reverserecipe.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class IngredientSearchActivity extends Fragment implements AsyncResponse {

	Button addButton;
	EditText editText;
	Button searchButton;
	Button deleteButton;
	ImageButton fromPantryButton;
	ImageButton fromMostUsedButton;
	ArrayList<String> ingredientList;
	ArrayList<String> mostUsedIngredients;
	ListView listView;
	String[] allIngredients;
	ListView selectedListView;
	ArrayAdapter<String> selectedAdapter;
	ArrayList<String> selectedFromPantry;
	List<Integer> selectedIndexesFromPantry;
	SharedPreferences prefs;
	String savedPantry;
	ArrayList<String> pantryList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_ingredient_search, container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		new GetAllIngredients(this).execute(); //populates auto complete
		
		prefs = getActivity().getSharedPreferences("reverseRecipe", Context.MODE_PRIVATE);
		savedPantry = prefs.getString("reverseRecipe.savedPantry","Didnt work");
		pantryList = new ArrayList<String>();
		
		try {
			pantryList = Utilities.jsonStringToArray(savedPantry);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//declares xml elements
	    fromPantryButton = (ImageButton)getView().findViewById(R.id.fromPantryButton);
	    fromMostUsedButton = (ImageButton)getView().findViewById(R.id.fromMostUsedButton);
		addButton = (Button)getView().findViewById(R.id.addIngredientButton);
		editText = (EditText)getView().findViewById(R.id.ingredientsSearch);
		selectedListView = (ListView)getView().findViewById(R.id.ingredientList);
		searchButton = (Button)getView().findViewById(R.id.searchButton);
		deleteButton = (Button)getView().findViewById(R.id.clearButton);

		ingredientList = new ArrayList<String>();
		
		selectedAdapter = new ArrayAdapter<String>(getView().getContext(), R.layout.listview_small, ingredientList);  
	    selectedListView.setAdapter(selectedAdapter);
	    
		addButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {

				String input = editText.getText().toString().toLowerCase(Locale.ENGLISH);

				//adds ingredients to list if they exist in database and are not already on list
				if (!"".equals(input) && Arrays.asList(allIngredients).contains(input) && !(ingredientList.contains(input))) {
					selectedAdapter.add(input);
					selectedAdapter.notifyDataSetChanged();
					editText.setText("");
				}				
			}   	
		});

		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(selectedAdapter.getCount() > 0) {
					GoToSearchRecipes();
				}
			}
		});
		
	    deleteButton.setOnClickListener(new View.OnClickListener(){

	    	//delete selected items from list and updates pantry list saved on phone
	    	@Override
			public void onClick(View arg0) {
	    		selectedAdapter.clear();
	    		selectedAdapter.notifyDataSetChanged();
			}    	
	    });
	    
    	fromPantryButton.setOnClickListener(new Button.OnClickListener(){
    		
    		@Override
    	   public void onClick(View arg0) { 
    			showSearchDialog(pantryList, R.string.my_pantry);
	   }});  
    	
    	fromMostUsedButton.setOnClickListener(new Button.OnClickListener(){
    		
    		@Override
    	   public void onClick(View arg0) {
    			DbHelper db = new DbHelper(getView().getContext());
    			mostUsedIngredients = db.getTop25Ingredients();
    			showSearchDialog(mostUsedIngredients, R.string.most_used);
	   }});
	    	            
	}

	//goes to search activity and starts a search with selected pantry ingredients
	private void GoToSearchRecipes() {

		//bundle stores the selected ingredients
		Intent intent_recipes = new Intent(getView().getContext(),SearchActivity.class);
		Bundle bundle = new Bundle();
		
		//add selected ingredients to bundle and start activity
		bundle.putStringArrayList("searchTerms", ingredientList);
		intent_recipes.putExtras(bundle);
		startActivity(intent_recipes);
	}

	@Override
	public void responseObtained(String output) {

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
	
	private void showSearchDialog(final ArrayList<String> list, int title){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final List<Integer> mSelectedItems = new ArrayList<Integer>();
		CharSequence[] ingredientSequence = list.toArray(new CharSequence[list.size()]);
		boolean[] checkedItems = new boolean[list.size()];
		
		//check items that are already present in selected list
		for (int i = 0; i < checkedItems.length; i++){
			
			for (int j = 0; j < selectedAdapter.getCount(); j++){
				if (selectedAdapter.getItem(j).equals(list.get(i))){
					checkedItems[i] = true;
				}
			}
			
		}
		
		builder.setTitle(title)
			.setMultiChoiceItems(ingredientSequence, checkedItems,
					new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int ingredient, boolean isChecked) {
							if (isChecked) {
								mSelectedItems.add(ingredient);
							} else if (mSelectedItems.contains(ingredient)) {
								mSelectedItems.remove(Integer
										.valueOf(ingredient));
							}
						}
					})
			.setPositiveButton(R.string.add_ingredient,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							for (int i = 0; i < mSelectedItems.size(); i++){								
								String ingredient = list.get(mSelectedItems.get(i));
								
								if (!(ingredientList.contains(ingredient))) {
									selectedAdapter.add(ingredient);
									selectedAdapter.notifyDataSetChanged();
								}
							}
						}
					})
			.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
						}
					});
		
		builder.create().show();
	}
}