package serializer.utils;

import java.util.Scanner;

/**
 * Has method for formatting JSON to make it look better
 * 
 * @author Collin Shoop
 */
public class JsonFormatter {

	public static String format(String json) {
		int tabCount = 0;
		StringBuilder pJsonBuilder = new StringBuilder();
		
		Scanner scanner = new Scanner(json);
		char[] chars = json.toCharArray();
		for(char c : chars) {
			boolean blockStart = (c == '{' || c == '[');
			boolean blockEnd = (c == '}' || c == ']');
			boolean printReturn = false;
			
			if (blockStart) {
				tabCount++;
				printReturn = true;
			}
			else if (c == ',') {
				printReturn = true;
			}
			
			if (blockEnd) {
				tabCount--;
				printReturn = true;
			} else {
				pJsonBuilder.append(c);
			}
			
			if (printReturn) {
				pJsonBuilder.append('\n');
				for(int i = 0; i < tabCount; i++) {
					pJsonBuilder.append("   ");
				}
			}
			
			if (blockEnd) {
				pJsonBuilder.append(c);
			}
		}
		scanner.close();
		
		return pJsonBuilder.toString();
	}
}
