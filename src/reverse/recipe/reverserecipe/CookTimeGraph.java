package reverse.recipe.reverserecipe;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class CookTimeGraph {
	public Intent getIntent(Context context) {
		DbHelper db = new DbHelper(context);
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
        renderer.setLabelsTextSize(20);
        renderer.setChartTitle("Cook Times");
        renderer.setChartTitleTextSize((float) 30);
        renderer.setLegendTextSize(25);
        renderer.setDisplayValues(false);
        
        // set custom messages for certain situations
        if (shortTime > mediumTime + longTime) {
        	renderer.setChartTitle("Wow, your really pushing the envolope there!");
        }
        
        
        return ChartFactory.getPieChartIntent(context, series, renderer, "Cook Times");

	}
}
