package reverse.recipe.reverserecipe;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class AnalyticsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_layout);
		setupActionBar();
		loadIngredientGraph();
		loadDifficultyGraph();
		loadCookTimeGraph();
		loadPrepTimeGraph();
	}
	
	private void loadPrepTimeGraph() {
		DbHelper db = new DbHelper(getBaseContext());
		// define the time windows
		int shortTime = db.getCountOfPrepTimeBetween(0, 10);
		int mediumTime = db.getCountOfPrepTimeBetween(11, 30);
		int longTime = db.getCountOfPrepTimeBetween(31, 500);
		// label the categories
		CategorySeries series = new CategorySeries("pie");
		series.add("10mins or less",shortTime);           
        series.add("10 to 30mins",mediumTime);
        series.add("over 30mins",longTime);
        // set the colors 
        int []colors = new int[]{Color.rgb(182, 193, 233), Color.rgb(148, 206, 156), Color.rgb(162, 108, 108)};
        
        DefaultRenderer renderer = new DefaultRenderer();
        for(int color : colors){
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            r.setDisplayBoundingPoints(true);
            renderer.addSeriesRenderer(r);
        }
        renderer.isInScroll();
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.BLACK);
        renderer.setShowLabels(true);
        renderer.setLabelsTextSize(15);
        renderer.setChartTitle("Prep Times");
        renderer.setChartTitleTextSize((float) 25);
        renderer.setShowLegend(false);
        renderer.setDisplayValues(false);

        renderer.setClickEnabled(true);
		LinearLayout prepLayout = (LinearLayout) findViewById(R.id.layout_prepTimeGraph);
		GraphicalView mChartView = ChartFactory.getPieChartView(getBaseContext(), series, renderer);
		OnClickListener viewListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PrepTimeGraph graph = new PrepTimeGraph();
				Intent graphIntent = graph.getIntent(getBaseContext());
				startActivity(graphIntent);
			}
		};
		mChartView.setOnClickListener(viewListener);
        prepLayout.addView(mChartView);
	}

	private void loadCookTimeGraph() {
		DbHelper db = new DbHelper(getBaseContext());
		// define the time windows
		int shortTime = db.getCountOfCookTimeBetween(0, 30);
		int mediumTime = db.getCountOfCookTimeBetween(31, 80);
		int longTime = db.getCountOfCookTimeBetween(81, 1000);
		// label the categories
		CategorySeries series = new CategorySeries("pie");
		series.add("30mins or less",shortTime);           
        series.add("30 to 80mins",mediumTime);
        series.add("over 80mins",longTime);
        // set the color of each category
        int []colors = new int[]{Color.rgb(182, 193, 233), Color.rgb(148, 206, 156), Color.rgb(162, 108, 108)};
        
        DefaultRenderer renderer = new DefaultRenderer();
        for(int color : colors){
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            r.setDisplayBoundingPoints(true);
            renderer.addSeriesRenderer(r);
        }
        renderer.isInScroll();
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.BLACK);
        renderer.setShowLabels(true);
        renderer.setLabelsTextSize(15);
        renderer.setChartTitle("Cook Times");
        renderer.setChartTitleTextSize((float) 25);
        renderer.setShowLegend(false);
        renderer.setDisplayValues(false);
        
        renderer.setClickEnabled(true);
		LinearLayout cookLayout = (LinearLayout) findViewById(R.id.layout_cookTimeGraph);
		GraphicalView mChartView = ChartFactory.getPieChartView(getBaseContext(), series, renderer);
		OnClickListener viewListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CookTimeGraph graph = new CookTimeGraph();
				Intent graphIntent = graph.getIntent(getBaseContext());
				startActivity(graphIntent);
			}
		};
		mChartView.setOnClickListener(viewListener);
        cookLayout.addView(mChartView);
	}

	private void loadDifficultyGraph() {
		DbHelper db = new DbHelper(getBaseContext());
		String easy = "Easy";
		String intermediate = "Intermediate";
		String hard = "Difficult";
		String hardest = "Expert";
		int easyCount = db.getCountOfDifficulty(easy);
		int intermediateCount = db.getCountOfDifficulty(intermediate);
		int hardCount = db.getCountOfDifficulty(hard);
		int hardestCount = db.getCountOfDifficulty(hardest);
		
		CategorySeries series = new CategorySeries("pie");
		series.add(easy, easyCount);           
        series.add(intermediate, intermediateCount);
        series.add(hard, hardCount);
        series.add(hardest, hardestCount);
        int []colors = new int[]{Color.rgb(182, 193, 233), Color.rgb(148, 206, 156), Color.rgb(162, 108, 108), Color.rgb(162, 108, 162)};
        
        DefaultRenderer renderer = new DefaultRenderer();
        for(int color : colors){
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            r.setDisplayBoundingPoints(true);
            renderer.addSeriesRenderer(r);
        }
        renderer.isInScroll();
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.BLACK);
        renderer.setShowLabels(true);
        renderer.setLabelsTextSize(15);
        renderer.setChartTitle("Cooking Difficulty");
        renderer.setChartTitleTextSize((float) 25);
        renderer.setShowLegend(false);
        renderer.setDisplayValues(false);

        renderer.setClickEnabled(true);
		LinearLayout diffLayout = (LinearLayout) findViewById(R.id.layout_difficultyGraph);
		GraphicalView mChartView = ChartFactory.getPieChartView(getBaseContext(), series, renderer);
		OnClickListener viewListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DifficultyGraph graph = new DifficultyGraph();
				Intent graphIntent = graph.getIntent(getBaseContext());
				startActivity(graphIntent);
			}
		};
		mChartView.setOnClickListener(viewListener);
        diffLayout.addView(mChartView);
	}

	private void loadIngredientGraph() {
		DbHelper db = new DbHelper(getBaseContext());
		// Get the ingredient names for the X axis
		ArrayList<String> ingredientList = db.getTop25Ingredients();
		CategorySeries series = new CategorySeries("Most Searched Ingredients");
		// Get the ingredient counts for the Y axis
		for (int i = 0; i < ingredientList.size(); i++) {
			series.add(db.getIngredientCount(ingredientList.get(i)));
		}
		
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		// Renderer is used to render the dataset to a graph
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.setBackgroundColor(Color.BLACK); // sets background colour
		mRenderer.setApplyBackgroundColor(true);
        mRenderer.setLegendHeight(125); // adjust the legend vertically
        // set sizes of text
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setAxisTitleTextSize(14);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setShowLegend(false);
        // set the margins around the graph
        mRenderer.setMargins(new int[] { 30, 40, 100, 0 });
        // set the color of the bars
        renderer.setColor(Color.rgb(184, 194, 194));

        mRenderer.setXAxisMin(0.5);
        mRenderer.setXAxisMax(10.5);
        mRenderer.setYAxisMin(0);
        mRenderer.setXLabels(0);
        
        mRenderer.setChartTitle("Most Searched Ingredients");

        for (int i = 0; i < ingredientList.size(); i++) {
        	mRenderer.addXTextLabel(i+1, ingredientList.get(i));
        }
        // space between the bars
        mRenderer.setBarSpacing(0.5);
       
        mRenderer.setXLabelsAngle(45);
        mRenderer.setXLabelsAlign(Align.LEFT);

        mRenderer.setClickEnabled(true);
		mRenderer.addSeriesRenderer(renderer);
		LinearLayout ingredientLayout = (LinearLayout) findViewById(R.id.layout_ingredientGraph);
		GraphicalView mChartView = ChartFactory.getBarChartView(getBaseContext(), dataset, mRenderer, Type.DEFAULT);
		OnClickListener viewListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IngredientGraph graph = new IngredientGraph();
				Intent graphIntent = graph.getIntent(getBaseContext());
				startActivity(graphIntent);
			}
		};
		mChartView.setOnClickListener(viewListener);
		ingredientLayout.addView(mChartView);
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
		case R.id.action_analytics:
			Intent analyticsIntent = new Intent(this,AnalyticsActivity.class);
			startActivity(analyticsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
