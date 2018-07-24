import java.util.*;
import java.io.*;

public class Commands {

// =============================================================================
// The CREATE TABLE function. Whenever "CREATE" is detected, this function is 
//   called. It creates a new relation with the values evaluated from an
//   expression
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static Table createCommand(ArrayList<String> sql_tokens) {	
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		ArrayList<String> attribute_names = new ArrayList<String>();
		ArrayList<String> attribute_domains = new ArrayList<String>();
		ArrayList<String> primary_keys = new ArrayList<String>();

		// Start the token index at the begining of the relation name
		Integer token_index = Grammar.skipToToken(sql_tokens, 0, "table") + 1;

		// Get the new relation name
		String relation_name = "";
		ArrayList<String> relation_name_ArrayList = Grammar.retrieveTokens(sql_tokens, token_index, "(", false);
		for (String string : relation_name_ArrayList) {
			relation_name += string + " ";
		}
		relation_name = relation_name.trim();
		token_index += relation_name_ArrayList.size() + 1;

		// Get the attribute list and their types
		for (int i = token_index; i < sql_tokens.size(); i++) {
			if (sql_tokens.get(i).equals("PRIMARY")) {
				token_index = i;
				break;
			}
			else if (sql_tokens.get(i).equals("VARCHAR")) {
				attribute_domains.add(sql_tokens.get(i) + " " + sql_tokens.get(i+2));
				i = i + 2;
				continue;
			}
			else if (sql_tokens.get(i).equals("INTEGER")) {
				attribute_domains.add(sql_tokens.get(i));
				continue;
			}
			else if (!sql_tokens.get(i).equals("(") && !sql_tokens.get(i).equals(")")) {
				attribute_names.add(sql_tokens.get(i));
			}
			else {
				//System.out.println("Skipping token... " + sql_tokens.get(i));
			}
		}

		// Create the Attributes
		for (int i = 0; i < attribute_names.size(); i++) {
			Attribute attribute = new Attribute(attribute_names.get(i), attribute_domains.get(i));
			attributes.add(attribute);
		}

		// Get the primary keys list
		for (int i = token_index; i < sql_tokens.size(); i++) {
			if (sql_tokens.get(i).equals(";")) {
				token_index = i;
				break;
			}
			else if (sql_tokens.get(i).equals("PRIMARY") || sql_tokens.get(i).equals("KEY")) {
				continue;
			}
			else if (!sql_tokens.get(i).equals("(") && !sql_tokens.get(i).equals(")")) {
				primary_keys.add(sql_tokens.get(i));
			}			
			else {
				//System.out.println("Skipping token... " + sql_tokens.get(i));				
			}
		}

		System.out.println("Table name:" + relation_name);
		System.out.println("Attribute Names:" + attribute_names);
		System.out.println("Types List:" + attribute_domains);
		System.out.println("Primary keys List:" + primary_keys);

		Table new_table = Engine.createTable(relation_name, attributes, primary_keys);
		return new_table;
	}

// =============================================================================
// The INSERT function. Whenever "INSERT" is detected, this function is 
//   called. It will attempt to add a new row to an existing relation, or add
//   all rows from another relation to an existing relation
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static void insertCommand(ArrayList<String> sql_tokens) {
		String relation_name = "";
		ArrayList<String> expression_ArrayList = new ArrayList<String>();

		Integer token_index = 2;

		// Gets the relation name
		for (int i = token_index; i < sql_tokens.size(); i++) {
			if (sql_tokens.get(i).equals("VALUES")) {
				token_index = i + 2;
				break;
			}
			else {
				relation_name += sql_tokens.get(i) + " ";
			}
		}
		relation_name = relation_name.trim();

		if (sql_tokens.get(token_index).equals("(")) {
			// Get the list of values to be inserted
			for (int i = token_index; i < sql_tokens.size(); i++) {
				if (sql_tokens.get(i).equals(";")) {
					token_index = i;
					break;
				}
				else if (!sql_tokens.get(i).equals("(") && !sql_tokens.get(i).equals(")")) {
					expression_ArrayList.add(sql_tokens.get(i));
				}
				else {
					//System.out.println("Skipping token... " + sql_tokens.get(i));
				}
			}

			System.out.println(relation_name + expression_ArrayList);
			Engine.insertRow(relation_name, expression_ArrayList);
		}
		else if (sql_tokens.get(token_index).equals("RELATION")) {
			for (int i = token_index; i < sql_tokens.size(); i++) {
				if (sql_tokens.get(i).equals(";")) {
					break;
				}
				else {
					expression_ArrayList.add(sql_tokens.get(i));
				}
			}
			Table expression_table = Grammar.evaluateExpression(expression_ArrayList);
			if (expression_table != null) {
				for (int i = 0; i < expression_table.relation.size(); i++) {

					// Get the row from the table and insert it
					Row row = expression_table.relation.get(i);
					Engine.insertRow(relation_name, row.values);
				}
			}
		}
		else {
			System.out.println("Invalid command detected. Failed to insert row.");
		}
	}

// =============================================================================
// The UPDATE function. Whenever "UPDATE" is detected, this function is 
//   called. It will attempt to update an existing row from an existing relation
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static void updateCommand(ArrayList<String> sql_tokens) {
		String relation_name = "";
		ArrayList<String> attributes = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> condition_ArrayList = new ArrayList<String>();

		Integer token_index = 1;
		// Get the relation name
		for (int i = token_index; i < sql_tokens.size(); i++) {
			if (sql_tokens.get(i).equals("SET")) {
				token_index = i;
				break;
			}
			else {
				relation_name += sql_tokens.get(i) + " ";
			}
		}
		relation_name = relation_name.trim();

		// Get the set of data
		Integer old_token_index = token_index;
		for (int i = old_token_index; i < sql_tokens.size() - 1; i++) {
			if (sql_tokens.get(i).equals("WHERE")) {
				token_index = i + 1;
				break;
			}
			else if (sql_tokens.get(i+1).equals("=")) {
				attributes.add(sql_tokens.get(i));
				values.add(sql_tokens.get(i+2));
			}
			else {
				continue;
			}
		}

		for (int i = token_index; i < sql_tokens.size(); i++) {
			condition_ArrayList.add(sql_tokens.get(i));
		}

		System.out.println("Table name:" + relation_name);
		System.out.println("Attribute List:" + attributes);
		System.out.println("Values List:" + values);
		System.out.println("Conditions List:" + condition_ArrayList);

		Engine.updateRow(relation_name, attributes, values, condition_ArrayList);
	}

// =============================================================================
// The DELETE function. Whenever "DELETE" is detected, this function is 
//   called. It will attempt to delete an existing row from an existing relation
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static void deleteCommand(ArrayList<String> sql_tokens) {
		String relation_name = "";
		ArrayList<String> condition_ArrayList = new ArrayList<String>();

		Integer token_index = 2;
		// Get the relation name
		for (int i = token_index; i < sql_tokens.size(); i++) {
			if (sql_tokens.get(i).equals("WHERE")) {
				token_index = i + 1;
				break;
			}
			else {
				relation_name += sql_tokens.get(i) + " ";
			}
		}
		relation_name = relation_name.trim();

		for (int i = token_index; i < sql_tokens.size(); i++) {
			condition_ArrayList.add(sql_tokens.get(i));
		}

		System.out.println("Table name:" + relation_name);
		System.out.println("Conditions List:" + condition_ArrayList);

		Engine.deleteRow(relation_name, condition_ArrayList);
	}

// =============================================================================
// The SHOQ function. Whenever "SHOW" is detected, this function is 
//   called. It will attempt to print an existing relation or one defined by an 
//   expression
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static String showCommand(ArrayList<String> sql_tokens) {
		// Get the expression ArrayList (or relation name)
		ArrayList<String> expression_ArrayList = Grammar.retrieveTokens(sql_tokens, 1, ";", true);

		// Evaluate and create the table
		Table expression_table = Grammar.evaluateExpression(expression_ArrayList);
		if (expression_table != null) {
			Engine.relations_database.put(expression_table.relation_name, expression_table);

			System.out.println("Table Name: " + expression_table.relation_name);
			String show_results = Engine.show(expression_table.relation_name);

			return show_results;
		}
		else {
			System.out.println("Table doesn't exist; failed to call Engine.show()");
			return null;
		}
	}

// =============================================================================
// The OPEN function. Whenever "OPEN" is detected, this function is 
//   called. It will attempt to read in an existing table from the table_data
//   directory
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static void openCommand(ArrayList<String> sql_tokens) {	
		String relation_name = Grammar.getRelationName(sql_tokens);
		System.out.println("Table Name: " + relation_name);
		Engine.openTable(relation_name.trim()); // Close table???
	}

// =============================================================================
// The WRITE function. Whenever "WRITE" is detected, this function is 
//   called. It will attempt to write an existing table to the table_data
//   directory
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static void writeCommand(ArrayList<String> sql_tokens) {
		String relation_name = Grammar.getRelationName(sql_tokens);
		System.out.println("Table Name: " + relation_name);
		Engine.writeTable(relation_name.trim());
	}

// =============================================================================
// The CLOSE function. Whenever "CLOSE" is detected, this function is 
//   called. It will delete a table from the local database, not the saved files
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static void closeCommand(ArrayList<String> sql_tokens) {	
		String relation_name = Grammar.getRelationName(sql_tokens);
		System.out.println("Table Name: " + relation_name);
		Engine.closeTable(relation_name.trim());
	}

// =============================================================================
// The DROP TABLE function. Whenever "DROP" is detected, this function is 
//   called. It will attempt to drop the specified relation
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static void dropCommand(ArrayList<String> sql_tokens) {	
		String relation_name = Grammar.getRelationName(sql_tokens);
		System.out.println("Table Name: " + relation_name);
		Engine.dropTable(relation_name.trim());
	}

// =============================================================================
// The EXIT function. Whenever "EXIT" is detected, this function is 
//   called. It will call the methods to end the program
// Parameters: 
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static void exitCommand() {
		Engine.exit();
	}
}