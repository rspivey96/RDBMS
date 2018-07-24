import java.util.*;
import java.io.*;

public class Grammar {
   
// =============================================================================
// The general grammar function. Essentially, this takes a line and tokenizes 
//   it, removing some unnecessary ones and rebuilding others.
// Parameters:
//   line: A String received from Standard Input, ready to be tokenized
// =============================================================================

	public static String parseLine(String line) {

		ArrayList<String> sql_tokens = new ArrayList<String>();

		// Tokenize the input and store in a vector
		String delimiters = "(){};=<>,&| \t\n\r\f";
		StringTokenizer st = new StringTokenizer(line, delimiters, true);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!token.trim().isEmpty() && !token.trim().equals(",")) {
				token = token.replaceAll(",", ""); // Remove all commas
				sql_tokens.add(token);
			 }
		}

		String[] operators = {"!", "<", ">", "="};
		for (int i = 1; i < sql_tokens.size(); i++) {

			// Reconnect the query signs
			if (sql_tokens.get(i-1).equals("<") && sql_tokens.get(i).equals("-")) {
				sql_tokens.set(i-1, "<-");
				sql_tokens.remove(i);
			}

			// Reconnect the double operators
			for (String operator : operators) {
				if (sql_tokens.get(i-1).equals(operator) && sql_tokens.get(i).equals("=")) {
					sql_tokens.set(i-1, operator + "=");
					sql_tokens.remove(i);
				}
			}

			// Reconnect the AND operator
			String[] logic_operators = {"&", "|"};
			for (String operator : logic_operators) {
				if (sql_tokens.get(i-1).equals(operator) && sql_tokens.get(i).equals(operator)) {
					sql_tokens.set(i-1, operator + operator);
					sql_tokens.remove(i);
				}
			}

		}

		// Call the correct method with the tokenized input 
		return callMethod(sql_tokens);
	}

// =============================================================================
// A function to figure out which method to call from the tokenized input
// Parameters:
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	private static String callMethod(ArrayList<String> sql_tokens) {

		System.out.println("Tokenized ArrayList: " + sql_tokens);

		tokenloop:
		for (String token : sql_tokens) {
			switch(token) {
				// Queries
				case "<-":
					System.out.println("QUERY invoked");
					Queries.queryQuery(sql_tokens);
					break tokenloop;
				case "select":
					System.out.println("SELECT invoked");
					Queries.selectQuery(sql_tokens);
					break tokenloop;
				case "project":
					System.out.println("PROJECT invoked");
					Queries.projectQuery(sql_tokens);
					break tokenloop;
				case "rename":
					System.out.println("RENAME invoked");
					Queries.renameQuery(sql_tokens);
					break tokenloop;
				case "+":
					System.out.println("SET UNION invoked");
					Queries.setUnionQuery(sql_tokens);
					break tokenloop;
				case "-":
					System.out.println("SET DIFFERENCE invoked");
					Queries.setDifferenceQuery(sql_tokens);
					break tokenloop;
				case "*":
					System.out.println("CROSS PRODUCT invoked");
					Queries.crossProductQuery(sql_tokens);
					break tokenloop;
				case "JOIN":
					System.out.println("NATURAL JOIN invoked");
					Queries.naturalJoinQuery(sql_tokens);
					break tokenloop;

				// Commands
				case "CREATE":
					System.out.println("CREATE TABLE invoked");
					Commands.createCommand(sql_tokens);
					break tokenloop;
				case "INSERT":
					System.out.println("INSERT invoked");
					Commands.insertCommand(sql_tokens);
					break tokenloop;
				case "UPDATE":
					System.out.println("UPDATE ROW invoked");
					Commands.updateCommand(sql_tokens);
					break tokenloop;
				case "DELETE":
					System.out.println("DELETE ROW invoked");
					Commands.deleteCommand(sql_tokens);
					break tokenloop;
				case "SHOW":
					System.out.println("SHOW invoked");
					String show_results = Commands.showCommand(sql_tokens);
					return show_results; // Return the results
				case "OPEN":
					System.out.println("OPEN invoked");
					Commands.openCommand(sql_tokens);
					break tokenloop;
				case "WRITE":
					System.out.println("OPEN invoked");
					Commands.writeCommand(sql_tokens);
					break tokenloop;
				case "CLOSE":
					System.out.println("CLOSE invoked");
					Commands.closeCommand(sql_tokens);
					break tokenloop;
				case "DROP":
					System.out.println("DROP TABLE invoked");
					Commands.dropCommand(sql_tokens);
					break tokenloop;
				case "EXIT":
					System.out.println("EXIT invoked");
					Commands.exitCommand();
					break tokenloop;
			}
		}
		return null;
	}

// =============================================================================
// A function to move the token_index *PAST* a specified token, effectively 
//   skipping it
// Parameters:
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
//   token: The specified token to skip
// =============================================================================

	public static Integer skipTokens(ArrayList<String> sql_tokens, String token) {
		for (int i = 0; i < sql_tokens.size(); i++) {
			if (!sql_tokens.get(i).equalsIgnoreCase(token)) {
				return i;
			}
		}
		return 0;
	}

// =============================================================================
// A function to move the token_index *TO* a specified token, effectively 
//   skipping *TO* it
// Parameters:
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
//   token: The specified token to skip to
// =============================================================================

	public static Integer skipToToken(ArrayList<String> sql_tokens, Integer token_index, String token) {
		for (int i = token_index; i < sql_tokens.size(); i++) {
			if (sql_tokens.get(i).equalsIgnoreCase(token)) {
				return i;
			}
		}
		return 0;
	}

// =============================================================================
// A function to retrieve all tokens from a certain token up to a specified 
//   token
// Parameters:
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
//   token_index: The starting token's index
//   token: The ending token 
//   value: A value to determine whether or not to include the ending token in 
//     the returned ArrayList
// =============================================================================

	public static ArrayList<String> retrieveTokens(ArrayList<String> sql_tokens, Integer token_index, String token, Boolean value) {
		ArrayList<String> temp_vector = new ArrayList<String>();

		for (int i = token_index; i < sql_tokens.size(); i++) {
			if (sql_tokens.get(i).equals(token)) {
				token_index = i + 1;
				if (value) {
					temp_vector.add(sql_tokens.get(i));			
				}
				break;
			}
			else {
				temp_vector.add(sql_tokens.get(i));
			}
		}
		return temp_vector;
	}

// =============================================================================
// A function to evaluate an expression recursively. Essentially, whichever
//   query function it comes across first, it evaluates, and then continues with
//   the evaluated expression until it reaches an end or an already-existing
//   relation name. 
// Parameters:
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static Table evaluateExpression(ArrayList<String> sql_tokens) {
		// Start off by evaluating the expression
		for (int i = 0; i < sql_tokens.size(); i++) {
			String token = sql_tokens.get(i);
			switch(token.toLowerCase()) {
				case "select":
					System.out.println("SELECT invoked");
					return Queries.selectQuery(sql_tokens);
				case "project":
					System.out.println("PROJECT invoked");
					return Queries.projectQuery(sql_tokens);
				case "rename":
					System.out.println("RENAME invoked");
					return Queries.renameQuery(sql_tokens);
				case "+":
					System.out.println("SET UNION invoked");
					return Queries.setUnionQuery(sql_tokens);
				case "-":
					System.out.println("SET DIFFERENCE invoked");
					return Queries.setDifferenceQuery(sql_tokens);
				case "*":
					System.out.println("CROSS PRODUCT invoked");
					return Queries.crossProductQuery(sql_tokens);
				case "join":
					System.out.println("NATURAL JOIN invoked");
					return Queries.naturalJoinQuery(sql_tokens);
			}
		}

		// If no expression is found, check if it is just a relation name
		if (isRelationName(sql_tokens)) {
			Table retrieved_table = Engine.relations_database.get(sql_tokens.get(0));
			return retrieved_table;
		}
		// No relation name or expression found
		else {
			return null;
		}
	}
 
// =============================================================================
// A function to determine if a given ArrayList of tokens only contains an 
//   already-existing relation name. This allows the above function to check
//   if an expression is algebraic or not. 
// Parameters:
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static Boolean isRelationName(ArrayList<String> sql_tokens) {
		Boolean value = true;
		Integer token_index = 0;

		// Remove leading parentheses
		for (int i = token_index; i < sql_tokens.size(); i++) {
			if (sql_tokens.get(i).equals("(")) {
				token_index = i + 1;
			}
			else {
				break;
			}
		}

		// Check if next token is a relation name
		String[] algebraic_expressions = {"select", "project", "rename", "+", "-", "*", "JOIN"};
		for (int i = token_index; i < sql_tokens.size(); i++) {
			for (String expression : algebraic_expressions) {

				// Check if the next token is actually an algebraic expression
				if (sql_tokens.get(i).equals(expression)) {
					value = false; 
				}
			}
		}
		return value;
	}

// =============================================================================
// A function that allows certain commands (DROP, SHOW, WRITE, etc.) to quickly
//   retrieve the relation name or determine if an expression is present 
// Parameters:
//   sql_tokens: An ArrayList containining tokenized psuedo-SQL
// =============================================================================

	public static String getRelationName(ArrayList<String> sql_tokens) {
		String relation_name = "";

		if (sql_tokens.contains("CREATE") ||
			sql_tokens.contains("SELECT") ||
			sql_tokens.contains("PROJECT") ||
			sql_tokens.contains("+") ||
			sql_tokens.contains("-") ||
			sql_tokens.contains("*") ||
			sql_tokens.contains("JOIN")) {
				System.out.println("Nested table detected in getRelationName");
				System.out.println("(This shouldn't happen!)");
		}
		else {
			for (String token : sql_tokens) {
				if (token.equals("OPEN") ||
					token.equals("CLOSE") ||
					token.equals("WRITE") ||
					token.equals("SHOW") ||
					token.equals("DROP") ||
					token.equals("TABLE")) {
					continue;
				}
				else if (token.equals(";")) {
					break;
				}
				else {
					relation_name += token + " ";					
				}
			}
		}
		relation_name = relation_name.trim();
		return relation_name;
	}
}