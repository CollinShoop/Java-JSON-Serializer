package serializer.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class<?> utility methods used when dealing with primitive classes. 
 * Identifies the primitive class types to be: boolean, char, byte, short, int, long
 * float, double, and void.
 * 
 * @author Collin Shoop
 */
public class ClazzUtils {

	private static final Set<Class<?>> WRAPPER_TYPES = new LinkedHashSet<Class<?>>(
			Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class,
					Float.class, Double.class, Void.class));

	private static final Set<Class<?>> PRIMITIVE_TYPES = new LinkedHashSet<Class<?>>(Arrays.asList(boolean.class,
			char.class, byte.class, short.class, int.class, long.class, float.class, double.class, void.class));

	// relates primitive type to it's respective wrapper type
	private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();
	static {
		Iterator<Class<?>> wrapperIterator = WRAPPER_TYPES.iterator();
		Iterator<Class<?>> primitiveIterator = PRIMITIVE_TYPES.iterator();
		for (int i = 0; i < PRIMITIVE_TYPES.size(); i++) {
			PRIMITIVE_WRAPPER_MAP.put(primitiveIterator.next(), wrapperIterator.next());
		}
	}

	/**
	 * Determines if the given class type is a primitive class or primitive
	 * wrapper class
	 * 
	 * @param clazz class type to check
	 * @return whether or not the given class type is primitive or of a
	 *         primitive wrapper
	 */
	public static boolean isSimpleType(Class<?> clazz) {
		return isPrimitive(clazz) || isWrapper(clazz);
	}

	/**
	 * Determines if the given class type is a primitive class
	 * 
	 * @param clazz class type to check
	 * @return whether or not the given class is primitive class
	 */
	public static boolean isPrimitive(Class<?> clazz) {
		return PRIMITIVE_TYPES.contains(clazz);
	}

	/**
	 * Determines if the given class type is a primitive wrapper class
	 * 
	 * @param clazz class type to check
	 * @return whether or not the given class is a primitive wrapper class
	 */
	public static boolean isWrapper(Class<?> clazz) {
		return WRAPPER_TYPES.contains(clazz);
	}

	/**
	 * Gets the wrapper class for the given primitive class type.
	 * 
	 * @param clazz primitive class
	 * @return the wrapper class for the given primitive or null if the given
	 *         class is not primitive.
	 */
	public static Class<?> getPrimitiveClassWrapper(Class<?> clazz) {
		return PRIMITIVE_WRAPPER_MAP.get(clazz);
	}
}
