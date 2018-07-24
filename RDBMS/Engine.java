import java.util.*;
import java.io.*;

public class Engine {

	// Public Variables
	public static HashMap<String, Table> relations_database = new HashMap<String, Table>();

// =============================================================================
// A function to create a new relation and add it to the database
// Parameters: 
//   relation_name: The user-defined relation name
//   attributes: An array of Attributes, which have a name and domain
//   keys: An array of attribute names to determine how a key is formed
// =============================================================================

	public static Table createTable(String relation_name, ArrayList<Attribute> attributes, ArrayList<String> keys) {
		// Check if the table already exists
		Table table = relations_database.get(relation_name);
		Table opened_table = openTable(relation_name);
		if (table != null) {
			System.out.println("Error: Table already exists. Failed to create.");
			return table;
		}
		else if (opened_table != null) {
			System.out.println("Error: Table already exists as serialized file. Failed to create.");
			return opened_table;
		}
		else {
			// Returns a table initialized with ID and specified attributes
			Table new_table = new Table(relation_name, attributes, keys);

			// Store the created table in the tables container
			relations_database.put(relation_name, new_table);

			System.out.println("Created table: " + relation_name);
			return new_table;
		}
	}

// =============================================================================
// A function to add a new row to an existing table
// Parameters:
//   relation_name: The name of the relation accepting the new row
//   values: A String Array of values that will make up the row
// =============================================================================

	public static void insertRow(String relation_name, ArrayList<String> values) {
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table doesn't exist; failed to insert row.");
		}
		else {
			Row row = new Row(values, table.createKey(values));

			// Only insert if all values are valid
			if (table.isRowValid(row) && 
				!row.key.equals("")) {

				// And if the row doesn't already exist
				if (table.getRow(row.key) == null) {
					table.addRow(row);
				}
				else {
					System.out.println("Error: Row already exists with key: " + row.key + "; failed to insert row.");				
				}
			}
			else {
				System.out.println("Error: Invalid value detected; failed to insert row.");				
			}
		}
	}

// =============================================================================
// A function to update an existing row
// Parameters:
//   relation_name: The name of the relation the row belongs to
//	 attributes: The attribute columns to update
//	 values: The new attribute values. This ArrayList corresponds with the
//	   attributes ArrayList
//	 tokenized_conditions: The conditions a row must meet
// =============================================================================

	public static void updateRow(String relation_name, ArrayList<String> attributes, ArrayList<String> values, ArrayList<String> tokenized_conditions) { 
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table doesn't exist; failed to update row.");
		}
		else {
			// Loop through all the rows of the table
			for (Row row : table.relation) {

				// Check if it meets the required conditions
				if (parseConditions(table, row.values, tokenized_conditions)) {

					// And only update if all values are valid
					if (table.isRowValid(row)) {

						// Check that the attribute to update exists
						for (int i = 0; i < values.size(); i++) {
							Integer update_attribute_index = table.getAttributeIndex(attributes.get(i));
							if (update_attribute_index >= 0) {

								// The row meets all conditions, update it
								table.updateRow(row.key, update_attribute_index, values.get(i));
							}
						}
					}
				}
			}
		}
	}
	
// =============================================================================
// A function to delete an existing row
// Parameters:
//   relation_name: The name of the relation the row belongs to
//	 tokenized_conditions: The conditions a row must meet in order to be deleted
// =============================================================================

	public static void deleteRow(String relation_name, ArrayList<String> tokenized_conditions) {
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table doesn't exist; failed to delete row.");
		}
		else {
			// Loop through all the rows of the table
			for (int i = 0; i < table.relation.size(); i++) {
				Row row = table.relation.get(i);

				// Check if it meets the required conditions
				if (parseConditions(table, row.values, tokenized_conditions)) {

					// The row meets all conditions, delete it
					table.deleteRow(row.key);
				}
			}
		}
	}
	
// =============================================================================
// A function to return a new table from a selection
// Parameters: 
//   relation_name: The name of the relation to perform the selection on
//	 tokenized_conditions: The conditions a row must meet in order to be 
//     selected
// =============================================================================

	public static Table selection(String relation_name, ArrayList<String> tokenized_conditions) {
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table doesn't exist; failed to select.");
			return null;
		}
		else {
			Table selection_table = new Table("Selection from " + relation_name, table.attributes, table.keys);	

			// Loop through all the rows of the table
			for (Row row : table.relation) {

				// Check if it meets the required conditions
				if (parseConditions(table, row.values, tokenized_conditions)) {

					// Row has met the condition, add it to the selection table
					selection_table.addRow(row);
				}
			}
			return selection_table;		
		}
	}

// =============================================================================
// A function to return a new table from a projection
// Parameters: 
//   relation_name: The name of the relation to perform the projection on
//	 attributes: The list of attributes to keep from the original table
// =============================================================================

	public static Table projection(String relation_name, ArrayList<String> attributes) {
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table doesn't exist; failed to project.");
			return null;
		}
		else {
			// Get the indices of the new attribute columns from the original table
			ArrayList<Integer> indicies = new ArrayList<Integer>();
			for (String attribute : attributes) {
				int i = table.getAttributeIndex(attribute);
				if (i >= 0) {
					indicies.add(i);
				}
			}

			// Update the attribute columns
			ArrayList<Attribute> new_attributes = new ArrayList<Attribute>();
			for(int i : indicies){
				new_attributes.add(table.attributes.get(i));
			}

			Table projection_table = new Table("Projection from " + relation_name, new_attributes, table.keys);

			// Loop through all the rows of the table
			for (Row original_row : table.relation) {

				// Get the specified attributes values using obtained indices
				ArrayList<String> projected_values = new ArrayList<String>();
				for(int i : indicies){
					projected_values.add(original_row.values.get(i));
				}

				// Create the new key
				String projected_key = projection_table.createKey(projected_values);

				// Create a new row with the new data
				Row projected_row = new Row(projected_values, projected_key);

				// And add it to the projected table, if it doesn't already exist
				if (projection_table.getRow(projected_row.key) == null) {
					projection_table.addRow(projected_row);
				}
			}
			return projection_table;
		}
	}

// =============================================================================
// A function to return a new table from with renamed attributes
// Parameters: 
//   relation_name: The name of the relation with attributes to rename
//	 attributes: The list of new, renamed attributes to apply to the relation
// =============================================================================

	public static Table rename(String relation_name, ArrayList<String> attributes) {
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table doesn't exist; failed to rename.");
			return null;
		}
		else {
			// Get a deep copy of the current attributes
			ArrayList<Attribute> new_attributes = new ArrayList<Attribute>();
			for (Attribute attribute : table.attributes) {
				Attribute new_attribute = new Attribute(attribute.name, attribute.domain);
				new_attributes.add(new_attribute);
			}

			// and rename them with the corresponding new attributes
			for (int i = 0; i < new_attributes.size() && i < attributes.size(); i++) {
				new_attributes.get(i).rename(attributes.get(i));
			}

			// Get an updated primary key ArrayList using the original table's stored indices
			ArrayList<String> new_keys = new ArrayList<String>();
			for (int i = 0; i < table.key_indices.size(); i++) {
				new_keys.add(new_attributes.get(table.key_indices.get(i)).name);
			}

			Table rename_table = new Table("Renaming from " + relation_name, new_attributes, new_keys);

			// Add all the original rows to the new table
			for(int i = 0; i < table.relation.size(); i++){
				Row row = table.relation.get(i);
				rename_table.addRow(row);
			}
			return rename_table;
		}
	}
// =============================================================================
// A function to return a new table, created by taking the union of two other 
//   tables
// Parameters: 
//   new_relation_name: The parser-defined relation name to apply to the new table
//   relation_name1: The relation name of the first table
//   relation_name2: The relation name of the second table
// =============================================================================

	public static Table setUnion(String new_relation_name, String relation_name1, String relation_name2) {
		// Check if both tables exists
		Table table1 = relations_database.get(relation_name1);
		Table table2 = relations_database.get(relation_name2);
		if (table1 == null|| table2 == null) {
			System.out.println("Error: Either one or both tables don't exist; failed to perform Set Union.");
			return null;
		}
		else {
			Table union_table = new Table(new_relation_name, table1.attributes, table1.keys);	

			// Loop through table 1 and record all non-duplicate rows
			for (Row row : table1.relation) {

				// Only add if it doesn't already exist
				if (union_table.getRow(row.key) == null) {
					union_table.addRow(row);
				}
			}

			// Loop through table 2 and record all non-duplicate rows
			for (Row row : table2.relation) {

				// Only add if it doesn't already exist
				if (union_table.getRow(row.key) == null) {
					union_table.addRow(row);
				}
			}

			return union_table;
		}
	}
	
// =============================================================================
// A function to return a new table, created by taking the difference of two
//   other tables
// Parameters: 
//   new_relation_name: The parser-defined relation name to apply to the new table
//   relation_name1: The relation name of the first table
//   relation_name2: The relation name of the second table
// =============================================================================

	public static Table setDifference(String new_relation_name, String relation_name1, String relation_name2) {
		// Check if both tables exists
		Table table1 = relations_database.get(relation_name1);
		Table table2 = relations_database.get(relation_name2);
		if (table1 == null|| table2 == null) {
			System.out.println("Error: Either one or both tables don't exist; failed to perform Set Difference.");
			return null;
		}
		else {		
			Table difference_table = new Table(new_relation_name, table1.attributes, table1.keys);	
			
			// Loop through the first table's rows
			for (Row row : table1.relation) {
				String key = row.key;

				// If either table2 or the difference table already contain the row, do not add it
				if (table2.getRow(key) != null || difference_table.getRow(key) != null) {
					//System.out.println(row.key + " was not added");
				}
				// Otherwise, add it
				else {
					//System.out.println(row.key + " was added");
					difference_table.addRow(row);
				}
			}
			return difference_table;
		}
	}
	
// =============================================================================
// A function to return a new table, created by taking the cross product of two
//   other tables
// Parameters: 
//   new_relation_name: The parser-defined relation name to apply to the new table
//   relation_name1: The relation name of the first table
//   relation_name2: The relation name of the second table
// =============================================================================

	public static Table crossProduct(String new_relation_name, String relation_name1, String relation_name2) {
		// Check if both tables exists
		Table table1 = relations_database.get(relation_name1);
		Table table2 = relations_database.get(relation_name2);
		if (table1 == null|| table2 == null) {
			System.out.println("Error: Either one or both tables don't exist; failed to perform Cross Product.");
			return null;
		}
		else {
			// Create a new ArrayList with the combined Attributes
			ArrayList<Attribute> cp_attributes = new ArrayList<Attribute>();
		    cp_attributes.addAll(table1.attributes);
		    cp_attributes.addAll(table2.attributes);

		    // Create a new ArrayList with the combined keys
			ArrayList<String> cp_keys = new ArrayList<String>();
		    cp_keys.addAll(table1.keys);
		    cp_keys.addAll(table2.keys);

			Table cp_table = new Table(new_relation_name, cp_attributes, cp_keys);

			// Loop through both tables and record all combined rows
			for (Row row1 : table1.relation) {
				for (Row row2 : table2.relation) {

					// Create a new ArrayList with the combined values
					ArrayList<String> new_values = new ArrayList<String>();
				    new_values.addAll(row1.values);
				    new_values.addAll(row2.values);

					// Create the new key
					String new_key = (String)row1.key + (String)row2.key;

					// Create the new row
					Row new_row = new Row(new_values, new_key);

					// Add it to the table
					cp_table.addRow(new_row);
				}
			}
			return cp_table;
		}
	}
	
// =============================================================================
// A function to return a new table, created by taking the cross product of two
//   other tables
// Parameters: 
//   new_relation_name: The parser-defined relation name to apply to the new table
//   relation_name1: The relation name of the first table
//   relation_name2: The relation name of the second table
// =============================================================================

	public static Table naturalJoin(String new_relation_name, String relation_name1, String relation_name2) {
		// Check if both tables exists
		Table table1 = relations_database.get(relation_name1);
		Table table2 = relations_database.get(relation_name2);
		if (table1 == null|| table2 == null) {
			System.out.println("Error: Either one or both tables don't exist; failed to perform Natural Join.");
			return null;
		}
		else {
			// Create a new ArrayList with the combined Attributes
			ArrayList<Attribute> nj_attributes = new ArrayList<Attribute>();
		    nj_attributes.addAll(table1.attributes);
		    nj_attributes.addAll(table2.attributes);

			// Remove the duplicate Attributes
			for (int i = 0; i < nj_attributes.size(); i++) {
				for (int p = 0; p < nj_attributes.size(); p++) {
					if (i != p) {
						if (nj_attributes.get(i).name.equals(nj_attributes.get(p).name)) {
							nj_attributes.remove(p);
						}
	                }
				}
			}

			// Create the new table with the joined attributes and table1's keys
			Table nj_table = new Table(new_relation_name, nj_attributes, table1.keys); 

			// Iterate through all Rows of both tables
			for (Row row1 : table1.relation) {
				for (Row row2 : table2.relation) {

					// Compare each primary key of table1 to table2
					if (row1.key.equals(row2.key)) {
						
						// Create a new ArrayList with the combined values
						ArrayList<String> new_values = new ArrayList<String>();
					    new_values.addAll(row1.values);
					    new_values.addAll(row2.values);

						// Get the new key
						String new_key = (String)row1.key + (String)row2.key;

						// Combine their elements
						Row combined_row = new Row(new_values, new_key);

						// Remove the duplicate values
						for (int i = 0; i < combined_row.values.size(); i++) {
							for (int p = 0; p < combined_row.values.size(); p++) {
								if (i != p) {
	                        		if (combined_row.values.get(i).equals(combined_row.values.get(p))) {
	                        			combined_row.values.remove(p);
	                        		}
	                   		 	}
							}
						}
						nj_table.addRow(combined_row);
					}
					else {
						//System.out.println("Row with key: " + row1.key + " did not match row with key: " + row2.key);
						//System.out.println("Failed to join");					
					}
				}
			}
			return nj_table;
		}
	}


// =============================================================================
// A function show an existing relation
// Parameters: 
//   relation_name: The name of the relation to be shown
// =============================================================================

	public static String show(String relation_name) {
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table doesn't exist; failed to show " + relation_name + ".");
			return null;
		}
		else {
			return table.show();
		}
	}

// =============================================================================
// A function to open a table from a serialized file
// Parameters:
//   relation_name: The name of the relation to be opened
// =============================================================================

  	public static Table openTable(String relation_name) {
  		if (relation_name.equals("OPEN_ALL_RELATIONS")) {
  						
			try (BufferedReader br = new BufferedReader(new FileReader("table_data/relations.txt"))) {
				String line;
				while ((line = br.readLine()) != null) {
					openTable(line);
				}
			} catch (IOException e) {
				System.out.println("Failed to open all files; relation.txt doesn't exist.");
			}
			return null;
  		}
  		else if (!tableExists(relation_name)) {
			try {
				Table read_table = null;

				FileInputStream file_in = new FileInputStream("table_data/" + relation_name + ".ser");
				ObjectInputStream in = new ObjectInputStream(file_in);

				// Read in the .ser data to a new Table object
				read_table = (Table)in.readObject(); // warning: [unchecked] unchecked cast
				in.close();
				file_in.close();

				// Store the read-in table in the tables container
				System.out.println("Table data found. Successfully opened " + relation_name + ".");
				relations_database.put(relation_name, read_table);
				return read_table;
			}
			catch(IOException i) {
				System.out.println("Error: Table data not found. Failed to open " + relation_name + ".");
				//i.printStackTrace();
				return null;
			}
			catch(ClassNotFoundException c) {
				System.out.println("Error: Table data not found. Failed to open " + relation_name + ".");
				//c.printStackTrace();
				return null;
			}
		}
		else {
			System.out.println("Error: Table is already opened. Failed to open " + relation_name + ".");
			return relations_database.get(relation_name);
		}
  	}

// =============================================================================
// A function to write a table to a serialized file
// Parameters: 
//   relation_name: The name of the relation to be written
// =============================================================================

	public static void writeTable(String relation_name) {
		try {
			// Check that the table exists
			Table table = relations_database.get(relation_name);
			if (table == null){
				System.out.println("Error: Table doesn't exist. Failed to write " + relation_name + ".");
			}
			else if (relation_name.contains("Temp")) {
				System.out.println("Error: is a view. Failed to write " + relation_name + ".");
			}
			else {
				// Create the .ser file
				FileOutputStream file_out = new FileOutputStream("table_data/" + relation_name + ".ser");
				ObjectOutputStream out = new ObjectOutputStream(file_out);

				// Write to the .ser file
				out.writeObject(table);
				out.close();
				file_out.close();
				System.out.println("Serialized data is saved in table_data/" + relation_name + ".ser");
			}
     	}
     	catch(IOException i) {
      		i.printStackTrace();
     	}
  	}

	
// =============================================================================
// A function to make the system forget about a table
// Parameters:
//   relation_name: The name of the relation to be closed
// =============================================================================

	public static void closeTable(String relation_name) {
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if(table == null){
			System.out.println("Error: Table doesn't exist. Failed to close " + relation_name + ".");
		}
		else {
			table.deleteTable();
			relations_database.remove(relation_name);
			System.out.println("Closed table: " + relation_name);
		}
	}

// =============================================================================
// A function to remove an existing relation from the database and disk
// Parameters:
//   relation_name: The name of the relation to be dropped
// =============================================================================

	public static void dropTable(String relation_name) {
		// Check if the table exists
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table doesn't exist; Failed to drop " + relation_name + ".");
		}
		else {
			// Remove local memory
			table.deleteTable();
			relations_database.remove(relation_name);
		}

		// Do this regardless whether the table exists in local memory or not
		File file = new File("table_data/" + relation_name + ".ser");
		if (file.delete()) {
			System.out.println("Dropped table: " + relation_name);
		}
		else {
			System.out.println("Error: Serialized table file doesn't exist; Failed to drop " + relation_name + ".");
		}
	}

// =============================================================================
// A function to close the database, saving all tables to serialized files
// =============================================================================

	public static void exit() {

		// Write all relations to a serialized file
		relations_database.forEach((relation_name, table) -> writeTable(relation_name));

		// Keep a record of all relations in a text file
		ArrayList<String> relation_names = new ArrayList<String>();
		relations_database.forEach((relation_name, table) -> relation_names.add(relation_name));

		Writer writer = null;
		try {
            writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("table_data/relations.txt"), "utf-8"));
            for (String relation_name : relation_names) {

            	// Don't save views
            	if (!relation_name.contains("Temp")) {
            		writer.write(relation_name + "\n");
            	}
			}
        }
        catch (IOException ex) {}
        finally {
            try {
            	writer.close();
            }
            catch (Exception ex) {}
        }
	}

// =============================================================================
// A function to parse an ArrayList of tokenized conditions. This is the first 
//   step of a 3 step process. This stage will break apart the tokenized 
//   conditions into individual conditions that will be evaluated in the next
//   step. 
// Parameters: 
//   table: The table containing the rows to check against a condition
//   row_values: The attribute values for the row we are checking.
//   token_ArrayList: The tokenized conditions, containing all conditions
// =============================================================================

	public static Boolean parseConditions(Table table, ArrayList<String> row_values, ArrayList<String> token_ArrayList){
		// Stores 1 complete comparison
		// e.g. "kind", "==", "dog"
		ArrayList<String> comparison_ArrayList = new ArrayList<String>();
		Boolean value = false; // default to false

		for (int i = 0; i < token_ArrayList.size(); i++){
			// The end of the comparison has been reached
			if (token_ArrayList.get(i).equals(";") || 
				token_ArrayList.get(i).equals(")")) {
				value = evaluateCondition(table, row_values, comparison_ArrayList);
				break;
			}
			// The end of the comparison has been reached, and it's a value
			else if (i == token_ArrayList.size() - 1) {
				comparison_ArrayList.add(token_ArrayList.get(i));
				value = evaluateCondition(table, row_values, comparison_ArrayList);	
				break;			
			}
			// Handle the && operator
			else if (token_ArrayList.get(i).equals("&&")) {
				ArrayList<String> and_comparison_ArrayList = new ArrayList<String>();
				for (int j = i+1; j < token_ArrayList.size(); j++){
					and_comparison_ArrayList.add(token_ArrayList.get(j));
				}
				value = (evaluateCondition(table, row_values, comparison_ArrayList) && parseConditions(table, row_values, and_comparison_ArrayList));
				break;
			}
			// Handle the || operator
			else if (token_ArrayList.get(i).equals("||")) {
				ArrayList<String> or_comparison_ArrayList = new ArrayList<String>();
				for (int j = i+1; j < token_ArrayList.size(); j++){
					or_comparison_ArrayList.add(token_ArrayList.get(j));
				}
				value = (evaluateCondition(table, row_values, comparison_ArrayList) || parseConditions(table, row_values, or_comparison_ArrayList));
				break;
			}
			// Handle nested comparisons
			else if(token_ArrayList.get(i).equals("(")){
				ArrayList<String> nested_comparison = new ArrayList<String>();
				for (int j = i+1; j < token_ArrayList.size(); j++){
					nested_comparison.add(token_ArrayList.get(j));
				}
				value = parseConditions(table, row_values, nested_comparison);
			}
			else {
				comparison_ArrayList.add(token_ArrayList.get(i));
			}
		}
		return value;
	}

// =============================================================================
// This is the second step of a 3 step process. This stage will determine what
//   parts of the row to look at, either an attribute column or a specified 
//    value. 
// Parameters: 
//   table: The table containing the rows to check against a condition
//   row_values: The attribute values for the row we are checking.
//   token_ArrayList: A single tokenized condition
// =============================================================================

	public static Boolean evaluateCondition(Table table, ArrayList<String> row_values, ArrayList<String> condition_ArrayList){
		Integer attribute1_index = table.getAttributeIndex(condition_ArrayList.get(0)); // kind -> 2
		Integer attribute2_index = table.getAttributeIndex(condition_ArrayList.get(condition_ArrayList.size() - 1)); // akind -> 2
		String attribute1 = "";
		String attribute2 = "";

		if (attribute1_index < 0) {
			attribute1 = condition_ArrayList.get(0);
		}
		else {
			attribute1 = row_values.get(attribute1_index);
		}

		String operator = condition_ArrayList.get(1);

		if (attribute2_index < 0) {
			attribute2 = condition_ArrayList.get(2);
		}
		else {
			attribute2 = row_values.get(attribute2_index);
		}

		// If the attribute exists, and the row meets the condition...
		return checkCondition(attribute1, operator, attribute2);
	}

// =============================================================================
// This is the last step of the 3 step process. This stage will look at the 
//   specified attribute value and the value from the row, and use the operator
//   to determine if it meets the condition or not.
// Parameters: 
//   attribute1: The first, specified value we are checking against
//   operator: The operator that tells us how to compare the two values
//   attribute2: The value from the row
// =============================================================================

	public static Boolean checkCondition(String attribute1, String operator, String attribute2){
		String integer_regex = "[0-9]+";

		// INTEGER case
		if ((attribute1.matches(integer_regex)) && (attribute2.matches(integer_regex))){
			switch(operator){
				case ">": 
					return (Integer.parseInt(attribute1) > Integer.parseInt(attribute2));
				case "<": 
					return (Integer.parseInt(attribute1) < Integer.parseInt(attribute2));
				case ">=": 
					return (Integer.parseInt(attribute1) >= Integer.parseInt(attribute2));
				case "<=": 
					return (Integer.parseInt(attribute1) >= Integer.parseInt(attribute2));
				case "==": 
					return attribute1.equals(attribute2);
				case "!=": 
					return !attribute1.equals(attribute2);
			}
		}
		// VARCHAR case
		else {
			switch(operator){
				case "==": 
					return attribute1.equals(attribute2);
				case "!=": 
					return !attribute1.equals(attribute2);
			}
		}

		// If no case is reached, return false
		return false;
	}

// =============================================================================
// A simple function to return True or False based on whether or not a table
//   exists
// Parameters: 
//   relation_name: The relation name of the table to be tested for existence
// =============================================================================

	public static Boolean tableExists(String relation_name) {
		Table table = relations_database.get(relation_name);
		if (table == null) {
			System.out.println("Error: Table and/or row don't exist.");
			return false;
		}
		else {
			return true;
		}
	}

}