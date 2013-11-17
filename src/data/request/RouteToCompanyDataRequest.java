package data.request;

import data.system.RouteToCompany;

public class RouteToCompanyDataRequest extends DataRequest<RouteToCompany> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2659842784217486017L;

	@Override
	public boolean isRequested(RouteToCompany dataEntry) {
		return true;
	}

}
