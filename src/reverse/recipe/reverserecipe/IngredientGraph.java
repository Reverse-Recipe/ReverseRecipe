package reverse.recipe.reverserecipe;

import java.util.ArrayList;
import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class IngredientGraph {
	public Intent getIntent(Context context) {
		DbHelper db = new DbHelper(context);
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
        mRenderer.setChartTitleTextSize(25);
        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setLegendTextSize(20);
        // set the margins around the graph
        mRenderer.setMargins(new int[] { 30, 40, 100, 0 });
        // set the color of the bars
        renderer.setColor(Color.rgb(184, 194, 194));

        mRenderer.setXAxisMin(0.5);
        mRenderer.setXAxisMax(10.5);
        mRenderer.setYAxisMin(0);
        mRenderer.setXLabels(0);

        for (int i = 0; i < ingredientList.size(); i++) {
        	mRenderer.addXTextLabel(i+1, ingredientList.get(i));
        }
        // space between the bars
        mRenderer.setBarSpacing(0.5);
       
        mRenderer.setXLabelsAngle(45);
        mRenderer.setXLabelsAlign(Align.LEFT);
        
        mRenderer.setChartTitle("Most Searched Ingredients");

        try {
        	// Customize a message for certain situations.
        	String topIngredient = ingredientList.get(0);
        	String secondIngredient = ingredientList.get(1);
        	if (db.getIngredientCount(ingredientList.get(0))/db.getIngredientCount(ingredientList.get(1)) > 2 && db.getIngredientCount(ingredientList.get(0)) > 10) {
        		mRenderer.setChartTitle("Oh man, that sure is a lot of " + topIngredient + "!");
        	}
        	if (ingredientList.size() < 10) {
        		mRenderer.setChartTitle("Hmm, not much variety in your diet");
        	}
        	if (mRenderer.getChartTitle().toString().length() == 0  && db.getIngredientCount(ingredientList.get(1)) > 4) {
        		mRenderer.setChartTitle("Looks like your one of those " + topIngredient + " and " + secondIngredient + " lover");
        	}
        } catch (Exception e) {}
        		
        		
		mRenderer.addSeriesRenderer(renderer);
		
		Intent intent = ChartFactory.getBarChartIntent(context, dataset, mRenderer, Type.DEFAULT);
		return intent;
	}
}
