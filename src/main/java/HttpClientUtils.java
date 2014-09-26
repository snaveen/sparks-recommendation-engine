import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * 
 */

/**
 * @author naveens
 *
 */
public class HttpClientUtils {
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String store="FlipKart";
		getCoupons(store);
	}

	public static String getCoupons(String store) throws IOException,
			ClientProtocolException {
		String url = "http://api.dataweave.in/v1/coupons/listByStore/?api_key=b20a79e582ee4953ceccf41ac28aa08d&page=1&per_page=10&store="+store;
		 
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
	 
		// add request header
//		request.addHeader("User-Agent", USER_AGENT);
		 HttpResponse response = client.execute(request);
	 
		System.out.println("Response Code : " 
	                + response.getStatusLine().getStatusCode());
	 
		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
	 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

}
