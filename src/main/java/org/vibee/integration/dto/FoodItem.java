package org.vibee.integration.dto;

public class FoodItem {
    private String foodItemId;

    private String foodItemName;

    private float foodItemPrice;

    public String getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(String foodItemId) {
        this.foodItemId = foodItemId;
    }

    public String getFoodItemName() {
        return foodItemName;
    }

    public void setFoodItemName(String foodItemName) {
        this.foodItemName = foodItemName;
    }


    public float getFoodItemPrice() {
        return foodItemPrice;
    }

    public void setFoodItemPrice(float foodItemPrice) {
        this.foodItemPrice = foodItemPrice;
    }
}
