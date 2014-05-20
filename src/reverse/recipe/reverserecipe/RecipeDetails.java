package reverse.recipe.reverserecipe;

import android.graphics.Bitmap;

//contains all recipe information
public class RecipeDetails { 		
	
	private String id;
	private String title;
	private int prepTime;
	private int inactiveTime;
	private int cookTime;
	private String difficulty;
	private int rating;
	private String yield;
	private int authorId;
	private String imageUrl;
	private String url;
	private String author;
	private Bitmap image;
	private int totalTime;
	private double relevance;
	private String[] ingredients;
	private String[] method;
	private String[] nutritionInfo;

	public RecipeDetails(String title, String id, String author, String imageUrl, String difficulty, int cookTime, int prepTime, int rating, String yield, double relevance) {
    	this.id = id;
    	this.title = title;
    	this.prepTime = prepTime;
    	//this.inactiveTime= inactiveTime;
    	this.cookTime = cookTime;
    	this.difficulty = difficulty;
		this.rating = rating;
		this.yield = yield;
		//this.authorId = authorId;
		this.imageUrl = imageUrl;
		this.author = author;
		this.totalTime = cookTime + prepTime;
		//this.url= url;
		this.relevance = relevance;
    }
	
	public double getRelevance() {
		return relevance;
	}
	
	public void setRelevance(double relev) {
		this.relevance = relev;
	}
	
	public Bitmap getImage() {
		return image;
	}
	
	public void setImage(Bitmap img) {
		this.image = img;
	}
	
	public int getTotalTime() {
		return totalTime;
	}
	
	public void setAuthor(String auth) {
		this.author = auth;
	}
	
	public String getAuthor() {
		return author;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
	}

	public int getInactiveTime() {
		return inactiveTime;
	}

	public void setInactiveTime(int inactiveTime) {
		this.inactiveTime = inactiveTime;
	}

	public int getCookTime() {
		return cookTime;
	}

	public void setCookTime(int cookTime) {
		this.cookTime = cookTime;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getYield() {
		return yield;
	}

	public void setYield(String yield) {
		this.yield = yield;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String[] getIngredients() {
		return ingredients;
	}

	public void setIngredients(String[] ingredients) {
		this.ingredients = ingredients;
	}

	public String[] getMethod() {
		return method;
	}

	public void setMethod(String[] method) {
		this.method = method;
	}

	public String[] getNutritionInfo() {
		return nutritionInfo;
	}

	public void setNutritionInfo(String[] nutritionInfo) {
		this.nutritionInfo = nutritionInfo;
	}
}
