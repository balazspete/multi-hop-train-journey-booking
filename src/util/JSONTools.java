package util;

import org.json.simple.JSONObject;

import data.MissingParameterException;

/**
 * Tools for handling JSONs
 * @author balazspete
 *
 */
public class JSONTools {

	/**
	 * Get the specified parameter from the JSONObject
	 * @param jobject
	 * @param name
	 * @return
	 * @throws MissingParameterException
	 */
	public static Object getParameter(JSONObject jobject, String name) throws MissingParameterException {
		Object object = jobject.get(name);
		checkIfNotNull(name, object);
		return object;
	}
	
	private static void checkIfNotNull(String name, Object object) throws MissingParameterException {
		if(object == null) 
			throw new MissingParameterException("Parameter `" + name + "` is not defined.");
	}
}
