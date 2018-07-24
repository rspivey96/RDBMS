import java.util.*;
import java.io.*;

public class Row implements Serializable {
	
	// Public Variables
	public ArrayList<String> values = new ArrayList<String>();
	public String key;

// =============================================================================
// The Row constructor
// Parameters:
//   values: The Attribute values that belong to the row
//   key: The row's Primary Key, created from its attributes
// =============================================================================

	Row(ArrayList<String> values, String key) {
		this.values = values;
		this.key = key;
	}
}