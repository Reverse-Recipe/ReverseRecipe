package reverse.recipe.reverserecipe;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class PrepTimeGraph {
	public Intent getIntent(Context context) {
		DbHelper db = new DbHelper(context);
		int shortTime = db.getCountOfPrepTimeBetween(0, 10);
		int mediumTime = db.getCountOfPrepTimeBetween(11, 30);
		int longTime = db.getCountOfPrepTimeBetween(31, 500);
		
		CategorySeries series = new CategorySeries("pie");
		series.add("10mins or less",shortTime);           
        series.add("10 to 30mins",mediumTime);
        series.add("over 30mins",longTime);
        int []colors = new int[]{Color.WHITE, Color.CYAN, Color.RED};
        
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
        renderer.setLabelsTextSize(20);
        renderer.setChartTitle("Prep Times");
        renderer.setChartTitleTextSize((float) 30);
        renderer.setLegendTextSize(25);
        renderer.setDisplayValues(false);
        
        if (shortTime > mediumTime + longTime) {
        	renderer.setChartTitle("Feeling a little lazy are we?");
        }
        return ChartFactory.getPieChartIntent(context, series, renderer, "Prep Times");

	}
}
