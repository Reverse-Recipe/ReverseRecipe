package reverse.recipe.reverserecipe;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class CookBookRecipeActivity extends Activity {

	RecipeDetails recipe = null;
	SharedPreferences prefs;
	CountDownTimer countDown;
	String pausedAt;
	Button pauseButton;
	Button stopButton;
	LinearLayout timerInput;
	LinearLayout timerDisplay;
	TextView timerLabel;
	TextView timerInputText;
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cookbook_recipe);
		setupActionBar();
        
		prefs = getSharedPreferences("reverseRecipe", Context.MODE_PRIVATE);

		Bundle bundle = getIntent().getExtras();
		String recipeId = bundle.getString("recipeId");
		int recipeRating = bundle.getInt("recipeRating");
		String recipeAuthor = bundle.getString("recipeAuthor");
		String recipeTitle = bundle.getString("recipeTitle");
		String recipeDifficulty = bundle.getString("recipeDifficulty");
		String recipeYield = bundle.getString("recipeYield");
		int recipeCookTime = bundle.getInt("recipeCookTime");
		int recipePrepTime = bundle.getInt("recipePrepTime");
		String recipeImageURL = bundle.getString("recipeImageURL");	
		String[] recipeMethod = bundle.getStringArray("recipeMethod");
		String[] recipeIngredients = bundle.getStringArray("recipeIngredients");

		recipe = new RecipeDetails(recipeTitle, recipeId, recipeAuthor, recipeImageURL, recipeDifficulty, recipeCookTime, recipePrepTime, recipeRating, recipeYield, 0);
		recipe.setMethod(recipeMethod);
		recipe.setIngredients(recipeIngredients);

		if (!"NULL".equals(recipeImageURL) && isOnline()) {
			new DownloadImageTask().execute(recipeImageURL);
		}

		setViews();
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
		getMenuInflater().inflate(R.menu.display_recipe, menu);
		menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.content_discard));
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_home:
			Intent homeIntent = new Intent(this,MainActivity.class);
			startActivity(homeIntent);
			return true;
		case R.id.action_analytics:
			Intent analyticsIntent = new Intent(this,AnalyticsActivity.class);
			startActivity(analyticsIntent);
			return true;
		case R.id.action_shopping_list:
			Intent shoppinglistIntent = new Intent(this,ShoppingListActivity.class);
			startActivity(shoppinglistIntent);
		case R.id.menu_save:
			deleteRecipe();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void setViews() {

		TextView recipeTitle = (TextView) findViewById(R.id.recipeTitle);
		TextView recipeAuthor = (TextView) findViewById(R.id.recipeAuthor);
		TextView recipeDifficulty = (TextView) findViewById(R.id.recipeDifficulty);
		TextView recipePrepTime = (TextView) findViewById(R.id.recipePrepTime);
		TextView recipeCookTime = (TextView) findViewById(R.id.recipeCookTime);
		TextView recipeRating = (TextView) findViewById(R.id.recipeRating);
		TextView recipeYield = (TextView) findViewById(R.id.recipeYield);
		
		recipeTitle.setText(recipe.getTitle(), TextView.BufferType.SPANNABLE);
		recipeAuthor.setText("Author: " + recipe.getAuthor());
		recipeDifficulty.setText("Difficulty: " + recipe.getDifficulty());
		recipePrepTime.setText("Prep Time: " + Integer.toString(recipe.getPrepTime()));
		recipeCookTime.setText("Cook Time: " + Integer.toString(recipe.getCookTime()));
		recipeRating.setText("Rating: " + Integer.toString(recipe.getRating()));
		recipeYield.setText("Yield: " + recipe.getYield());
	
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
	}

	//Downloads Images From URL
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {		

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
			try {
				Drawable d = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(result, 140, 110, true));
				TextView recipeImage = (TextView)findViewById(R.id.recipeTitle);
				recipeImage.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
			} catch (Exception e) {

			}
		}
	}

	public boolean isOnline() {
		ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

		if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
			return false;
		}
		return true; 
	}
	
	public void deleteRecipe() {
		
		JSONArray jsonArray;
		String recipeJSON = prefs.getString("reverseRecipe.savedCookBook", "None Found");
		if (recipeJSON != "None Found") {
			try {
				jsonArray = new JSONArray(recipeJSON);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				jsonArray = new JSONArray();
				e.printStackTrace();
			}
		} else {
			jsonArray = new JSONArray();
		}

		int position = -1;
		for (int x = 0; x < jsonArray.length(); x++) {
			try {
				if (recipe.getId().equals(jsonArray.getJSONObject(x).getString("Id"))) {
					position = x;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Deletes Recipe If Deselected
		JSONArray newList = new JSONArray();     
		int len = jsonArray.length();
		for (int i=0;i<len;i++)
		{ 
			//Excluding the item at position
			if (i != position) 
			{
				try {
					newList.put(jsonArray.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
				
		prefs.edit().putString("reverseRecipe.savedCookBook", newList.toString()).commit(); 
		
		Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
		
		finish();
	}
	
	
	public void startTimer(View view) {
		
		timerInputText = (TextView) findViewById(R.id.timerInputText);
		timerLabel = (TextView)findViewById(R.id.timerLabel);
		Long startTime = Long.valueOf(timerInputText.getText().toString()) * 60000;
		timerInput = (LinearLayout)findViewById(R.id.timerInput);
		timerDisplay = (LinearLayout)findViewById(R.id.timerDisplay);
		timerInput.setVisibility(View.GONE);
		timerDisplay.setVisibility(View.VISIBLE);
		pauseButton = (Button)findViewById(R.id.pauseCooking);
		stopButton = (Button)findViewById(R.id.stopCooking);
		
		pauseButton.setOnClickListener(new Button.OnClickListener(){
    		
    		@Override
    	   public void onClick(View arg0) {
    			
    			if (pauseButton.getText().equals("Pause")){
	    			countDown.cancel();
	    			pausedAt = timerLabel.getText().toString();
	    			pauseButton.setText("Resume");
    			} else {
    				
    				String[] splitTimer = pausedAt.split(":");
    				long minutes = Long.parseLong(splitTimer[0]);
    				long seconds = Long.parseLong(splitTimer[1]);
    				long startTime = (minutes * 60 + seconds) * 1000;
    				
    				countDown = initiateTimer(startTime);	
    				countDown.start();
    				pauseButton.setText("Pause");
    			}
	   }});
		
		stopButton.setOnClickListener(new Button.OnClickListener(){
    		
    		@Override
    	   public void onClick(View arg0) {
    			countDown.cancel();
    			timerInput.setVisibility(View.VISIBLE);
    			timerDisplay.setVisibility(View.GONE);
    			timerInputText.setText("");
    			
	   }});
		
		countDown = initiateTimer(startTime);	
		countDown.start();

	}
	

	public CountDownTimer initiateTimer(long startTime){
		
		countDown = new CountDownTimer(startTime, 1000) {
			
			
			
			
			 public void onTick(long millisUntilFinished) {
				 
				 long minutes = millisUntilFinished / 1000 / 60;
				 long seconds = millisUntilFinished / 1000 % 60;
				 timerLabel = (TextView)findViewById(R.id.timerLabel);
				 
				 timerLabel.setText(String.format("%02d", minutes) + ":" + String.format("%02d",seconds));
			 }

			 public void onFinish() {
				 timerInput.setVisibility(View.VISIBLE);
				 timerDisplay.setVisibility(View.GONE);
				 timerInputText.setText("");
				 
				 AlertDialog.Builder builder = new AlertDialog.Builder(CookBookRecipeActivity.this);
					
				 builder.setTitle("Timer Finished!")
				 	.setMessage("Your timer has finished")
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});
				
				builder.create().show();
			 }
		};
		
		return countDown;
	}

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
        listDataHeader.add("Ingredients");
        listDataHeader.add("Method");
 
        // Adding child data
        List<String> ingreds = new ArrayList<String>();
        
        for (int x = 0; x < recipe.getIngredients().length; x++) {
            ingreds.add(recipe.getIngredients()[x]);
        }
 
        List<String> method = new ArrayList<String>();
        for (int x = 0; x < recipe.getMethod().length; x++) {
            method.add(String.valueOf(x+1) + ".  " + recipe.getMethod()[x]);
        }
 
        listDataChild.put(listDataHeader.get(0), ingreds); // Header, Child data
        listDataChild.put(listDataHeader.get(1), method);
    }

}
