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
		ArrayList<String> ingredientList = db.getTop25Ingredients();
		CategorySeries series = new CategorySeries("Most Searched Ingredients");
		for (int i = 0; i < ingredientList.size(); i++) {
			series.add(db.getIngredientCount(ingredientList.get(i)));
		}
		
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setApplyBackgroundColor(true);
        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setLegendHeight(125);
        mRenderer.setChartTitleTextSize(25);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setLegendTextSize(20);
        mRenderer.setMargins(new int[] { 30, 40, 100, 0 });
        renderer.setColor(Color.rgb(99, 184, 255));
		//renderer.setDisplayChartValues(true);
        mRenderer.setXAxisMin(0.5);
        mRenderer.setXAxisMax(10.5);
        mRenderer.setYAxisMin(0);
        mRenderer.setXLabels(0);
        //mRenderer.setYAxisMax(25);
        for (int i = 0; i < ingredientList.size(); i++) {
        	mRenderer.addXTextLabel(i+1, ingredientList.get(i));
        }
        mRenderer.setBarSpacing(0.5);
        mRenderer.setXLabelsAngle(45);
        mRenderer.setXLabelsAlign(Align.LEFT);
        //mRenderer.setShowGrid(true);
        //mRenderer.setGridColor(Color.GRAY);
        try {
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
