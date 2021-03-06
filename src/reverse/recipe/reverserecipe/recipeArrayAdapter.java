package reverse.recipe.reverserecipe;

import java.util.ArrayList;

import reverse.recipe.reverserecipe.RecipeDetails;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
//import android.widget.ImageView;
import android.widget.TextView;

public class recipeArrayAdapter  extends ArrayAdapter<RecipeDetails>  {

	private static class ViewHolder {
		TextView title;
		TextView author;
		TextView difficulty;
		TextView time;
		ImageView image;
	}

	public recipeArrayAdapter(Context context, ArrayList<RecipeDetails> values) {
		super(context, R.layout.recipelayoutbuilder, values);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position 
		RecipeDetails recipe = getItem(position);

		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.recipelayoutbuilder, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.titleLabel);
			viewHolder.author = (TextView) convertView.findViewById(R.id.authorLabel);
			viewHolder.difficulty = (TextView) convertView.findViewById(R.id.difficultyLabel);
			viewHolder.time = (TextView) convertView.findViewById(R.id.timeLabel);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.image = (ImageView) convertView.findViewById(R.id.icon);
		}

		// Populate the data into the template view using the data object
		viewHolder.title.setText(recipe.getTitle());
		viewHolder.author.setText("Author: " + recipe.getAuthor());
		viewHolder.difficulty.setText("Difficulty: " + recipe.getDifficulty());
		viewHolder.time.setText("Time: " + recipe.getTotalTime() + " Minutes");

		if (recipe.getImageUrl() != null) {
			viewHolder.image.setImageBitmap(recipe.getImage());
		}

		// Return the completed view to render on screen
		return convertView;
	} 

}