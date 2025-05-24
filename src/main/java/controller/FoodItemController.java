package controller;



import java.util.List;

import model.FoodItem;

public interface FoodItemController {
	
	public boolean addFoodItem(FoodItem f);
	
	public List<FoodItem> getAllData();
	
	public boolean deleteFoodItem(int id);

	public List<FoodItem> getFoodItemById(int id);
	
	public boolean editFoodItem(FoodItem f);
	

}
