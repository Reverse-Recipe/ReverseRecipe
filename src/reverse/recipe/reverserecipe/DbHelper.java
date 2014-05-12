package reverse.recipe.reverserecipe;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "reverseRecipe";
	private static final String TABLE_INGREDIENTS = "ingredientsUsage"; 
    private static final String KEY_INGREDIENT = "ingredient";
    private static final String KEY_COUNT = "count";

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creates the table specified in above constants
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + "("
                + KEY_INGREDIENT + " TEXT PRIMARY KEY," + KEY_COUNT + " INT" + ")";
        db.execSQL(CREATE_INGREDIENTS_TABLE);
    }

	//not used
    @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);	 
        onCreate(db);	
	}
	
	//adds the ingredient to the database, if it already exists exception is thrown and handled
	public void addIngredient(String ingredient){
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_INGREDIENT, ingredient);
		values.put(KEY_COUNT, 0);
		
		try {
			db.insertOrThrow(TABLE_INGREDIENTS, null, values);
		}
		catch (SQLException e) {
		    //already in db
		}
		db.close();
	}
	
	//++ the specified ingredients count
	public void addToIngredientCount(String ingredient){
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_COUNT, getIngredientCount(ingredient) + 1);
		
		db.update(TABLE_INGREDIENTS, values, KEY_INGREDIENT + " = \"" + ingredient + "\"", null);
		
		db.close();
	}
	
	//returns top 25 most searched ingredients in descending order
	public ArrayList<String> getTop25Ingredients(){
		
		ArrayList<String> countList = new ArrayList<String>();
		String selectQuery = "SELECT  * FROM " + TABLE_INGREDIENTS + " ORDER BY " + KEY_COUNT + " DESC LIMIT 25";
		SQLiteDatabase db = this.getWritableDatabase();
	    
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
	        do {
	        	String name = cursor.getString(0);
	        	countList.add(name);
	        	
	        } while (cursor.moveToNext());
	    }
		
		cursor.close();
		
		return countList;
	}
	
	//returns number of times ingredient has been searched
	public int getIngredientCount(String ingredient){
		
		String countQuery = "SELECT " + KEY_COUNT + " FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_INGREDIENT + " = \"" + ingredient + "\"";
		SQLiteDatabase db = this.getReadableDatabase();
		int count = 0;
		
        Cursor cursor = db.rawQuery(countQuery, null);
        
        if (cursor.moveToFirst()) {
	        do {
	        	count = cursor.getInt(0);
	        	
	        } while (cursor.moveToNext());
	    }
        
        cursor.close();
		
		return count;
	}
}
