package reverse.recipe.reverserecipe;

public class RecipeDetails { 		
	
	private int id;
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
	private String[] ingredients;
	private String[] method;
	private String[] nutritionInfo;

	public RecipeDetails(int id, String title, int prepTime, int inactiveTime, int cookTime, String difficulty,int rating, String yield, int authorId, String imageUrl, String url) {
    	this.id = id;
    	this.title = title;
    	this.prepTime = prepTime;
    	this.inactiveTime= inactiveTime;
    	this.cookTime = cookTime;
    	this.difficulty = difficulty;
		this.rating = rating;
		this.yield = yield;
		this.authorId = authorId;
		this.imageUrl = imageUrl;
		this.url= url;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
