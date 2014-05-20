package reverse.recipe.reverserecipe;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class DifficultyGraph {
	public Intent getIntent(Context context) {
		DbHelper db = new DbHelper(context);
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
        int []colors = new int[]{Color.GREEN, Color.WHITE, Color.MAGENTA, Color.RED};
        
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
        renderer.setChartTitle("Cooking Difficulty");
        renderer.setChartTitleTextSize((float) 30);
        renderer.setLegendTextSize(25);
        renderer.setDisplayValues(false);
        
        if (easyCount > 0.6*(easyCount+intermediateCount+hardCount)) {
        	renderer.setChartTitle("Why not try something harder...");
        } else if ((hardCount+hardestCount) > 0.75*(easyCount+intermediateCount+hardCount+hardestCount)) {
        	renderer.setChartTitle("You are a masterchef!");
        } else if ((hardCount+hardestCount) > 0.5*(easyCount+intermediateCount+hardCount+hardestCount)) {
        	renderer.setChartTitle("Seems your quite handy in the kitchen!");
        }
        
        return ChartFactory.getPieChartIntent(context, series, renderer, "Cooking Difficuty");

	}
}
