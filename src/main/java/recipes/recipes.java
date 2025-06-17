/**
 * 
 */
package recipes;

import recipes.dao.DbConnection;
import recipes.service.RecipeService;

/**
 * 
 */
public class recipes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new RecipeService().createAndPopulateTables();
		//DbConnection.getConnection();		
		
	}

}
