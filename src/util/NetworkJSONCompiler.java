package util;

import java.io.*;
import java.util.*;

import org.joda.time.LocalTime;
import org.json.simple.*;
import org.json.simple.parser.*;

import com.sun.tools.jdi.LinkedHashMap;

import data.MissingParameterException;

public class NetworkJSONCompiler {
	
	private static class Section {
		
		private JSONObject section = new JSONObject();
		private String A, B;
		private long length;
		
		public Section(JSONObject json) throws MissingParameterException {
			section.put("cost", JSONTools.getParameter(json, "cost"));
			length = (Long) JSONTools.getParameter(json, "length");
			section.put("length", length);
			A = (String) JSONTools.getParameter(json, "A");
			B = (String) JSONTools.getParameter(json, "B");
		}
		
		public JSONObject toJSON(boolean reverse) {
			if(reverse) {
				section.put("start", B);
				section.put("end", A);
			} else {
				section.put("start", A);
				section.put("end", B);
			}
			
			return section;
		}
		
		public long getLength() {
			return length;
		}
	}

	public static Map compile(JSONArray stations, JSONArray routes) throws MissingParameterException, IOException {
		LinkedHashMap network = new LinkedHashMap();
		
		verifyStations(stations);
		network.put("stations", stations);
		
		Vector _routes = compileRoutes(routes);
		network.put("routes", _routes);
		
		return network;
	}
	
	private static void verifyStations(JSONArray stations) throws IllegalArgumentException {
		for(Object entry : stations) {
			JSONObject station = (JSONObject) entry;
			Set<String> keys = station.keySet();
			
			if(!(keys.contains("id") &&
					keys.contains("name") &&
					keys.contains("waitTime"))) 
				return;
		}
	}
	
	private static Vector compileRoutes(JSONArray routes) throws MissingParameterException, IOException {
		Vector result = new Vector();
		
		for (int a = 0; a<routes.size(); a++) {
			JSONObject rawRoute = (JSONObject) routes.get(a);
			
			String id = (String) JSONTools.getParameter(rawRoute, "routeID");
			long maxPassengers = (Long) JSONTools.getParameter(rawRoute, "maxPassengers");
			
			JSONArray rawSections = (JSONArray) JSONTools.getParameter(rawRoute, "route");
			Vector<Section> sections = new Vector<Section>();
			for(Object rawSection : rawSections) {
				sections.add(new Section((JSONObject) rawSection));
			}
			
			Vector<Section> reverseSections = (Vector<Section>) sections.clone();
			Collections.reverse(reverseSections);
			
			int sectionsSize = sections.size();
			
			JSONArray starts = (JSONArray) JSONTools.getParameter(rawRoute, "startTimes");
			int startsCount = 0;
			for(int _i = 0; _i< starts.size(); _i++) {
				LinkedHashMap route = new LinkedHashMap();
				route.put("maxPassengers", maxPassengers);
				
				JSONObject _start = (JSONObject) starts.get(_i);
				JSONObject start = (JSONObject) _start.clone();
				String from = (String) JSONTools.getParameter(start, "from");
				LocalTime time = new LocalTime(JSONTools.getParameter(start, "time"));
				
				Vector<Section> _sections;
				boolean reverse;
				if(id.startsWith(from)) {
					reverse = false;
					_sections = sections;
				} else {
					reverse = true;
					_sections = reverseSections;
				}
				
				int i = 0;
				JSONArray arr = new JSONArray();
				for (Section s : _sections) {
					JSONObject _s = s.toJSON(reverse);
					_s.put("time", time.toString());
					arr.add(_s.clone());
					time = time.plusSeconds(((int) s.getLength())+300);
				}
				
				route.put("routeID", ((JSONObject) arr.get(0)).get("start") + "-" + ((JSONObject) arr.get(sectionsSize-1)).get("end") + "-" +startsCount);
				route.put("route", arr);
				
				result.add(route);
				
				startsCount++;
			}	
		}
		
		return result;
	}
	
	
	public static void main(String[] args) {
		if(args[0].equalsIgnoreCase("--help")) {
			System.out.println("java NetworkJSONCompiler <stationsAray> <routesArray> [<output>]");
			return;
		}
		
		JSONParser parser = new JSONParser();
		
		try {
			JSONArray stations = (JSONArray) parser.parse(new FileReader(args[0]));
			JSONArray routes = (JSONArray) parser.parse(new FileReader(args[1]));
			
			Map result = compile(stations, routes);
			
			PrintStream out;
			if(args.length == 2 || args[2].equalsIgnoreCase("stdout")) {
				out = System.out;
			} else {
				out = new PrintStream(new File(args[2]));
			}
			
			StringWriter writer = new StringWriter();
			JSONValue.writeJSONString(result, writer);
			out.print(writer.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
