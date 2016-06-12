package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtils {

	public static String readFile(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		StringBuilder strBuilder = new StringBuilder();
		while (scanner.hasNextLine()) {
			strBuilder.append(scanner.nextLine());
			strBuilder.append("\n");
		}
		scanner.close();
		return strBuilder.toString();
	}

}
