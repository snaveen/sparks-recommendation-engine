
import static spark.Spark.*;

import java.awt.image.DataBuffer;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import spark.Request;
public class DealsServer {
	static List<String> reqFields=Arrays.asList("coupon_title","coupon_link","store","crawl_time");

	
	
	public static void main(String[] args) {
		   
	      get("/fetchDeals", (req, res) -> sendDeals(req.queryParams("deviceId")));
	      
	      post("/saveMessage", (req, res) -> parseMessages(req));
	   
	   }
	
	public static String sendDeals(String deviceId){
		
		JsonTransformer transformer = new JsonTransformer();
		MongoClient mongo = null;
		try {
			mongo = new MongoClient( "localhost" , 27017 );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DB db = mongo.getDB("mydb");
		DBCollection table = db.getCollection("categories");
		
		BasicDBObject searchQuery2 
	    = new BasicDBObject().append("message.deviceId", deviceId);
 
		DBCursor cursor2 = table.find(searchQuery2);
		String category = "";
		String latLong = "";
		
		while (cursor2.hasNext()) {
			DBObject obj = cursor2.next();
			Map data = (Map)obj.get("message");
			category = data.get("category").toString();
			latLong = data.get("location").toString();
		}
	 
		List coupons = fetchDealsFromExternalSite(category, latLong);					
		return transformer.render(coupons);
	}
	
	private static List fetchDealsFromExternalSite(String category,
			String latLong) {
		String resultJsonStr=null;
		Map result=null;
		try{
			resultJsonStr=HttpClientUtils.getCoupons(category);
			JsonTransformer transformer=new JsonTransformer();
			result=transformer.parse(resultJsonStr);
		}catch(Exception ex){
			System.out.println("Oops Check it");
		}
		
		
		List couponList=extractData((ArrayList<String>)result.get("data"));
//		System.out.println(couponList);
		return couponList;
		
	}

	private static List extractData(List inputCoupons) {
		List outputCoupons=new ArrayList();
		for (Object object : inputCoupons) {
			Map<String,Object> map=(Map<String,Object>)object;
			Map<String,Object> outputMap=new HashMap<String,Object>();
			for (Map.Entry<String, Object> entry : map.entrySet())
			{
			    if(reqFields.contains(entry.getKey())){
			      	outputMap.put(entry.getKey(), entry.getValue());
			    }
			}
			outputCoupons.add(outputMap);
		}
		return outputCoupons;
	}

	public static String parseMessages(Request req){
		JsonTransformer transformer = new JsonTransformer();
		String body = req.body();
		System.out.println("Sent the message : "+body);
		Map data = (Map) transformer.parse(body);
		saveData(data);
		return "success";
	}
	
	public static String getGroupon (){
		return "";
	}     
	
	public static String saveData(Map data){
		MongoClient mongo = null;
		try {
			mongo = new MongoClient( "localhost" , 27017 );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DB db = mongo.getDB("mydb");
		DBCollection table = db.getCollection("messages");
		DBCollection categories = db.getCollection("categories");
		
		BasicDBObject document = new BasicDBObject();
		document.put("message", data);
		
		BasicDBObject categoryDoc = new BasicDBObject();
		
		Map categoryData = new HashMap();
		
		String categoryInfo = getCategory(data.get("deviceId").toString());
		
		categoryData.put("category", categoryInfo);
		categoryData.put("deviceId", data.get("deviceId"));
		categoryData.put("location", data.get("location"));
		
		categoryDoc.put("message", categoryData);
		
		categories.insert(categoryDoc);
		
		table.insert(document);
		
		return "success";
	}
	
	private static String getCategory(String deviceId) {
		// TODO Auto-generated method stub
		return "flipkart";		
	}

	public static String convertDealsToMessages(String json){
		return "";
	}
	
}
