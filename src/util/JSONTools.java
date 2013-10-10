package util;

import org.json.simple.JSONObject;

import data.MissingParameterException;

public class JSONTools {

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
