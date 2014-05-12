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

public class IngredientGraph {
	public Intent getIntent(Context context) {
		DbHelper db = new DbHelper(context);
		ArrayList<String> ingredientList = db.getTop25Ingredients();
		CategorySeries series = new CategorySeries("Ingredients");
		for (int i = 0; i < ingredientList.size(); i++) {
			series.add(ingredientList.get(i), db.getIngredientCount(ingredientList.get(i)));
		}
		
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setApplyBackgroundColor(true);
		renderer.setColor(-65000);
		mRenderer.addSeriesRenderer(renderer);
		
		Intent intent = ChartFactory.getBarChartIntent(context, dataset, mRenderer, Type.DEFAULT);
		return intent;
	}
}
