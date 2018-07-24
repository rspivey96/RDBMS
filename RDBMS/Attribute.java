import java.util.*;
import java.io.*;

public class Attribute implements Serializable {
	
	// Public Variables
	public String name;
	public String domain;
	public Integer varchar_length = 0;

// =============================================================================
// The Attribute constructor
// Parameters:
//   attribtue_name: The user-generated attribute name e.g. "Player Name"
//   attribute_domain: The attribute's domain, either VARCHAR or INTEGER
// =============================================================================

	Attribute(String attribute_name, String attribute_domain) {
		name = attribute_name;

		// Check for the domain
		if (attribute_domain.equals("INTEGER")) {
			domain = attribute_domain;
		}
		else if (attribute_domain.contains("VARCHAR")) {
			String[] attribute_domain_array = attribute_domain.split("\\s");
			if (attribute_domain_array.length > 1) {
				domain = attribute_domain_array[0];
				varchar_length = Integer.parseInt(attribute_domain_array[1]);
			}
			else {
				System.out.println("Missing VARCHAR length for attribute!");
			}
		}
		else {
			System.out.println("Invalid attribute_domain detected: " + attribute_domain);
		}
	}

// =============================================================================
// A function to check if a given attribute fits its defined domain
// Parameters:
//   value: A value to be inserted into a relation's attribute column
//     e.g. "Joe" or "15"
// =============================================================================

	public boolean isValid(String value) {
		String integer_regex = "^\\d+";
		String varchar_regex = "^\"[a-zA-Z][a-zA-Z0-9_]*\"";

		if (value.matches(integer_regex) && 
			domain.equals("INTEGER")) {
			//System.out.println(value + " added!");			
			return true;
		}
		else if (value.matches(varchar_regex) && 
			domain.equals("VARCHAR") &&
			value.toCharArray().length <= varchar_length) {
			//System.out.println(value + " added!");			
			return true;
		}
		else if (value.matches(varchar_regex) && 
			domain.equals("VARCHAR") &&
			value.toCharArray().length >= varchar_length) {
			System.out.println(value + " exceeds VARCHAR size limit.");
			return false;
		}
		else {
			System.out.println(value + " does not match its domain: " + domain);
			System.out.println("Must be either 'VARCHAR(X)' or 'INTEGER'");
			return false;
		}
	}

// =============================================================================
// A function to rename an already existing attribute
// Parameters:
//   new_attribute_name: The new attribute name
//     e.g. "aname" or "akind"
// =============================================================================

	public void rename(String new_attribute_name) {
		name = new_attribute_name;
	}

}