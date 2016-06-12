import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import datamodel.ComplexTypeA;
import serializer.Property;
import serializer.Serializer;
import serializer.utils.JsonFormatter;
import utils.FileUtils;

public class main {

	/**
	 * Tests the functionality of the Serializer class
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Serializer serializer = new Serializer();
		serializer.disableProperty(Property.OUTPUT_NULL_PROPERTIES);
		String jsonString = FileUtils.readFile(new File("json.json"));
		ComplexTypeA object = null;
		try {
			object = serializer.asObject(jsonString, ComplexTypeA.class);
			System.out.println("Object serialization successful");
			System.out.println();
			System.out.println("Turning object back into JSON...");
			String actualJson = serializer.asString(object);
			System.out.println("Object as pretty JSON:");
			System.out.println(JsonFormatter.format(actualJson));
			try {
				JSONObject jsonObject = new JSONObject(actualJson); // validate
				System.out.println("The JSON is valid");
			} catch (JSONException e) {
				System.out.println("The JSON is not valid:");
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println("Object serialization failed");
		}

	}

}
