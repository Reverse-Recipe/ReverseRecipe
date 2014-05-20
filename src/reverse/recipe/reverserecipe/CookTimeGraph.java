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
		int shortTime = db.getCountOfCookTimeBetween(0, 30);
		int mediumTime = db.getCountOfCookTimeBetween(31, 80);
		int longTime = db.getCountOfCookTimeBetween(81, 1000);
		
		CategorySeries series = new CategorySeries("pie");
		series.add("30mins or less",shortTime);           
        series.add("30 to 80mins",mediumTime);
        series.add("over 80mins",longTime);
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
        renderer.setChartTitle("Cook Times");
        renderer.setChartTitleTextSize((float) 30);
        renderer.setLegendTextSize(25);
        renderer.setDisplayValues(false);
        
        if (shortTime > mediumTime + longTime) {
        	renderer.setChartTitle("Wow, your really pushing the envolope there!");
        }
        
        
        return ChartFactory.getPieChartIntent(context, series, renderer, "Cook Times");

	}
}
