package serializer;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import serializer.utils.ClazzUtils;

/**
 * Uses reflection to serialize and deserialize JSON into Java objects.
 * 
 * @author Collin Shoop
 */
public class Serializer {

	private final static char QUOTE = '\"';

	private Set<Property> enabledProperties = new HashSet<>();

	/**
	 * Creates a default instance of the given class type and tries to fill it
	 * with the given JSON string. Supports arrays, primitives, and nested
	 * objects.
	 * 
	 * @param json json string to deserialize into an instance of the given
	 *        class type
	 * @param clazz class type to initialize
	 * @return a new instance of the given class type from the given JSON
	 *         string.
	 */
	public <T> T asObject(String json, Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		JSONObject jsonObject = new JSONObject(json);

		T object = null;
		try {
			object = initializeObject(clazz);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		if (object != null) {
			for (Field field : fields) {
				if (jsonObject.has(field.getName())) {
					Object innerJsonObject = jsonObject.get(field.getName());
					Object innerObject = null;
					if (innerJsonObject instanceof String) {
						innerObject = asBasicObject((String) innerJsonObject, field.getType());
					} else if (innerJsonObject instanceof JSONObject) {
						innerObject = asObject(innerJsonObject.toString(), field.getType());
					} else if (innerJsonObject instanceof JSONArray) {
						if (field.getType().isArray()) {
							JSONArray jsonArray = (JSONArray) innerJsonObject;
							Class<?> arrayComponentType = field.getType().getComponentType();
							innerObject = Array.newInstance(arrayComponentType, jsonArray.length());
							for (int i = 0; i < jsonArray.length(); i++) {
								Object jsonArrayObject = jsonArray.get(i);
								if (jsonArrayObject instanceof String) {
									Array.set(innerObject, i,
											asBasicObject(jsonArrayObject.toString(), arrayComponentType));
								} else {
									Array.set(innerObject, i, asObject(jsonArrayObject.toString(), arrayComponentType));
								}
							}
						}
					} else {
						// the value of this key is null so it is left alone.
					}
					
					// make this field accessible so it's value can be set
					field.setAccessible(true);
					try {
						field.set(object, innerObject);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// ignore
					}
				}
			}
		}

		return object;
	}

	/**
	 * Creates a new instance of the given class type with the value in the
	 * given text. All wrapper classes and the string class have a constructor
	 * which takes a string. This constructor is used to create a new instance
	 * of that class.
	 * 
	 * @param text text representation of the value of the string or primitive
	 *        being created
	 * @param clazz class type of String or a primitive/primitive wrapper class
	 *        type.
	 * @return a new instance of the given class type with the value in the
	 *         given text.
	 */
	@SuppressWarnings("unchecked")
	private <T> T asBasicObject(String text, Class<T> clazz) {
		T returnObject = null;
		Class<?> wrapperType = clazz;

		// check for primitive class type which have no constructors
		if (ClazzUtils.isPrimitive(clazz)) {
			// get the wrapper class for this primitive so that there is a
			// constructor to use
			wrapperType = ClazzUtils.getPrimitiveClassWrapper(clazz);
		}
		try {
			// find a constructor for this class which takes a string (including
			// String)
			Constructor<?> wrapperConstructor = wrapperType.getConstructor(String.class);

			// use this constructor to make a new instance
			returnObject = (T) wrapperConstructor.newInstance(text);
		} catch (InstantiationException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return returnObject;
	}

	/**
	 * Creates the default constructor of the given class type to create a new
	 * instance of that class.
	 * 
	 * @param clazz class type
	 * @return a new default instance of the given class type
	 * @throws NoSuchMethodException this is thrown when the given class does
	 *         not have a default constructor
	 */
	@SuppressWarnings("unchecked")
	private <T> T initializeObject(Class<T> clazz) throws NoSuchMethodException {
		Constructor<?> defaultConstructor;
		T newInstance = null;
		try {
			defaultConstructor = clazz.getConstructor();
			newInstance = (T) defaultConstructor.newInstance();
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodException("Class type " + clazz.getName() + " is missing default constructor.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newInstance;
	}

	/**
	 * Converts the given object into a JSON string. Only object members with a
	 * predictable getter method will be used. A predictable getter method for a
	 * member with the name "variable" would have the name "getVariable".
	 * 
	 * @param object object to serialize
	 * @return a JSON serialization of the given object.
	 * @throws Exception
	 */
	public String asString(Object object) {
		StringBuilder jsonBuilder = new StringBuilder();
		if (object == null) {
			if (enabledProperties.contains(Property.OUTPUT_NULL_PROPERTIES)) {
				jsonBuilder.append("null");
			}
		} else {
			Class<?> clazz = object.getClass();
			if (object instanceof String || ClazzUtils.isSimpleType(clazz)) {
				jsonBuilder.append(QUOTE);
				jsonBuilder.append(String.valueOf(object));
				jsonBuilder.append(QUOTE);
			} else if (clazz.isArray()) {
				jsonBuilder.append(toJsonArray(object));
			} else {
				jsonBuilder.append("{");

				boolean firstField = true;
				for (Field field : clazz.getDeclaredFields()) {
					Method getterMethod = null;
					try {
						getterMethod = clazz.getMethod(getExpectedGetterName(field.getName()));
					} catch (Exception e) {
						// this variable doesn't have a getter so skip it
					}
					// Object value = field.get(object);
					if (getterMethod != null) {
						Object fieldValue = null;
						try {
							fieldValue = getterMethod.invoke(object);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}

						// recurse
						String valueString = asString(fieldValue);
						if (!valueString.isEmpty()) {
							if (firstField) {
								firstField = false;
							} else {
								jsonBuilder.append(",");
							}
							jsonBuilder.append(QUOTE);
							jsonBuilder.append(field.getName());
							jsonBuilder.append(QUOTE);
							jsonBuilder.append(":");
							jsonBuilder.append(valueString);
						}
					}
				}
				jsonBuilder.append("}");
			}
		}

		return jsonBuilder.toString();
	}

	/**
	 * Interprets the given object as an array and turns it into the respective
	 * JSON formated array.
	 * 
	 * @param object must be of type array
	 * @return the respective JSON formatted array of the given array.
	 */
	private String toJsonArray(Object object) {
		StringBuilder arrayStringBuilder = new StringBuilder("[");

		boolean firstPrinted = false;
		for (int i = 0; i < Array.getLength(object); i++) {
			Object nextObject = Array.get(object, i);
			if (nextObject != null) {
				if (firstPrinted) {
					arrayStringBuilder.append(",");
				} else {
					firstPrinted = true;
				}
				arrayStringBuilder.append(asString(nextObject));
			}
		}

		arrayStringBuilder.append("]");
		return arrayStringBuilder.toString();
	}

	/**
	 * Gets the expected name of the getter method for the variable with the
	 * given name.
	 * 
	 * @param varName name of a variable
	 * @return the expected name of the getter method for the variable with the
	 *         given name.
	 */
	private String getExpectedGetterName(String varName) {
		char firstChar = Character.toUpperCase(varName.charAt(0));
		return "get" + firstChar + varName.substring(1);
	}

	/**
	 * Enables the given property
	 * 
	 * @param property the property to enable
	 */
	public void enableProperty(Property property) {
		enabledProperties.add(property);
	}

	/**
	 * Disables the given property
	 * 
	 * @param property the property to disable
	 */
	public void disableProperty(Property property) {
		enabledProperties.remove(property);
	}

}
