package reverse.recipe.reverserecipe;

import java.util.ArrayList;
import java.util.Arrays;

import reverse.recipe.reverserecipe.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class IngredientSearchActivity extends Fragment implements AsyncResponse {

	Button addButton;
	EditText editText;
	ArrayList<String> ingredientList;
	ListView listView;
	ArrayAdapter<String> adapter;
	String[] allIngredients;
	
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

		//declares xml elements
		addButton = (Button)getView().findViewById(R.id.addIngredientButton);
		editText = (EditText)getView().findViewById(R.id.ingredientsSearch);
		listView = (ListView)getView().findViewById(R.id.ingredientList);

		ingredientList = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(getView().getContext(), android.R.layout.simple_expandable_list_item_1, ingredientList);

		listView.setAdapter(adapter);
		addButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {

				String input = editText.getText().toString();

				//adds ingredients to list if they exist in database and are not already on list
				if (Arrays.asList(allIngredients).contains(input) && !(ingredientList.contains(input))) {
					adapter.add(input);
				}				
			}   	
		});
		
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
