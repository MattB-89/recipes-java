/**
 * 
 */
package recipes;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.entity.Category;
import recipes.entity.Ingredient;
import recipes.entity.Recipe;
import recipes.entity.Step;
import recipes.entity.Unit;
import recipes.exception.DbException;
import recipes.service.RecipeService;


/**
 * 
 */
public class Recipes {

	private Scanner scanner = new Scanner(System.in);
	private RecipeService recipeService = new RecipeService();
	private Recipe curRecipe;
	
	//@formatter:off
	private List<String> operations = List.of(
			"1) Create and populate all tables",
			"2) Add a recipe",
			"3) List recipes",
			"4) Select working recipe",
			"5) Add ingredient to current recipe",
			"6) Add step to current recipe",
			"7) Add category to current recipe",
			"8) Modify step in current recipe",
			"9) Delete recipe"
	);
				
	//@formatter:on			
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		new Recipes().displayMenu();
	}
	
	private void displayMenu() {
		boolean done = false;
		
		while(!done) {			
			
			try {
				int operation = getOperation();	
				
				switch(operation) {
					case -1:
						done = exitMenu();
						break;
					case 1:
						createTables();
						break;
					case 2:
						addRecipe();
						break;
					case 3:
						listRecipes();
						break;
					case 4:
						setCurrentRecipe();
						break;
					case 5:
						addIngredientToCurrentRecipe();
						break;
					case 6:
						addStepToCurrentRecipe();
						break;
					case 7:
						addCategoryToCurrentRecipe();
						break;
					case 8:
						modifyStepInCurrentRecipe();
						break;
					case 9:
						deleteRecipe();
						break;
					default:
						System.out.println("\n" + operation + " is not valid.  Try again.");
				}
			}
			catch(Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
		}
	}

	private void deleteRecipe() {
		listRecipes();
		Integer recipeId = getIntInput("Enter the ID of the recipe to delete");
		
		if(Objects.nonNull(recipeId)) {
			recipeService.deleteRecipe(recipeId);
			
			System.out.println("You have deleted recipe " + recipeId);
			
			if(Objects.nonNull(curRecipe) && curRecipe.getRecipeId().equals(recipeId)) {
				curRecipe = null;
			}
		}
	}

	private void modifyStepInCurrentRecipe() {
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe first.");
			return;
		}
		
		List<Step> steps = recipeService.fetchSteps(curRecipe.getRecipeId());
		
		System.out.println("\nSteps for current recipe");
		
		steps.forEach(step -> System.out.println("   " + step));
		
		Integer stepId = getIntInput("Enter step ID of step to modify");
		
		if(Objects.nonNull(stepId)) {
			String stepText = getStringInput("Enter new step text");
			
			if(Objects.nonNull(stepText)) {
				Step step = new Step();
				
				step.setStepId(stepId);
				step.setStepText(stepText);
				
				recipeService.modifyStep(step);
				curRecipe = recipeService.fetchRecipeById(curRecipe.getRecipeId());
			}
		}
	}

	private void addCategoryToCurrentRecipe() {
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe first.");
			return;
		}
		
		List<Category> categories = recipeService.fetchCategories();
		
		categories.forEach(category -> System.out.println("   " + category.getCategoryName()));
		
		String category = getStringInput("Enter the category to add");
		
		if(Objects.nonNull(category)) {
			recipeService.addCategoryToRecipe(curRecipe.getRecipeId(), category);
			curRecipe = recipeService.fetchRecipeById(curRecipe.getRecipeId());
		}
	}

	private void addStepToCurrentRecipe() {
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe first.");
			return;
		}
		
		String stepText = getStringInput("Enter the step text");
		
		if(Objects.nonNull(stepText)) {
			Step step = new Step();
			
			step.setRecipeId(curRecipe.getRecipeId());
			step.setStepText(stepText);
			
			recipeService.addStep(step);			
			curRecipe = recipeService.fetchRecipeById(step.getRecipeId());
		}
	}

	private void addIngredientToCurrentRecipe() {
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe first.");
			return;
		}
		String name = getStringInput("Enter the ingredient name");
		String instruction = getStringInput("Enter an instruction if any (like \"finely chopped\")");
		Double inputAmount = getDoubleInput("Enter the ingredient amount (like 0.25)");
		List<Unit> units = recipeService.fetchUnits();
		
		BigDecimal amount = Objects.isNull(inputAmount) ? null : new BigDecimal(inputAmount).setScale(2);
		
		System.out.println("Units:");
		
		units.forEach(unit -> System.out.println("   " + unit.getUnitId() + ": "
				+ unit.getUnitNameSingular() + "(" + unit.getUnitNamePlural() + ")"));
		
		Integer unitId = getIntInput("Enter a unit ID (press Enter for none)");
		
		Unit unit = new Unit();
		unit.setUnitId(unitId);
		
		Ingredient ingredient = new Ingredient();
		ingredient.setRecipeId(curRecipe.getRecipeId());
		ingredient.setUnit(unit);
		ingredient.setIngredientName(name);
		ingredient.setInstruction(instruction);
		ingredient.setAmount(amount);
		
		recipeService.addIngredient(ingredient);
		curRecipe = recipeService.fetchRecipeById(ingredient.getRecipeId());
	}

	private void setCurrentRecipe() {
		List<Recipe> recipes = listRecipes();
		
		Integer recipeId = getIntInput("Select a recipe ID");
		
		curRecipe = null;
		
		for(Recipe recipe : recipes) {
			if(recipe.getRecipeId().equals(recipeId)) {
				curRecipe = recipeService.fetchRecipeById(recipeId);
				break;
			}
		}
		
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nInvalid recipe selected.");
		}
	}

	private List<Recipe> listRecipes() {
		List<Recipe> recipes = recipeService.fetchRecipes();
		
		System.out.println("\nRecipes:");
		
		recipes.forEach(
				recipe -> System.out.println("    " + recipe.getRecipeId() + ": " + recipe.getRecipeName()));
		
		return recipes;
	}

	private void addRecipe() {
		String name = getStringInput("Enter the recipe name");
		String notes = getStringInput("Enter the recipe notes");
		Integer numServings = getIntInput("Enter number of servings");
		Integer prepMinutes = getIntInput("Enter prep time in minutes");
		Integer cookMinutes = getIntInput("Enter cook time in minutes");

		LocalTime prepTime = minutesToLocalTime(prepMinutes);
		LocalTime cookTime = minutesToLocalTime(prepMinutes);

		Recipe recipe = new Recipe();
		
		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(numServings);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);
		
		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipe:\n" + dbRecipe);
		
		//curRecipe = recipeService.fetchRecipeById(dbRecipe.getRecipeId());
	}

	private LocalTime minutesToLocalTime(Integer numMinutes) {
		int min = Objects.isNull(numMinutes) ? 0 : numMinutes;
		int hours = min / 60;
		int minutes = min % 60;
		
		return LocalTime.of(hours, minutes);
	}

	private void createTables() {
		recipeService.createAndPopulateTables();
		System.out.println("\nTables created and populated.");
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu.");
		return true;
	}

	private int getOperation() {
		printOperations();
		Integer op = getIntInput("\nEnter an operation number (press Enter to quit)");
		
		return Objects.isNull(op) ? -1 : op;
	}	

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do:");
		
		operations.forEach(op -> System.out.println("   " + op));
		
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nYou are not working with a recipe.");
		} else {
			System.out.println("\nYou are working with recipe " + curRecipe);
		}
	}
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Integer.parseInt(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Double.parseDouble(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}
	
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();
		
		return line.isBlank() ? null : line.trim();
	}

}
