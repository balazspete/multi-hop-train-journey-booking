package data.system;

import java.util.Map.Entry;

/**
 * An object representing a mapping between a route and a company (name - same as {@link NodeInfo}:name)
 * @author Balazs Pete
 *
 */
public class RouteToCompany implements Entry<String, String> {

	private String route, company;
	
	public RouteToCompany(String route, String company) {
		this.route = route;
		this.company = company;
	}
	
	@Override
	public String getKey() {
		return route;
	}

	@Override
	public String getValue() {
		return company;
	}

	@Override
	public String setValue(String value) {
		return this.company = value;
	}
}
