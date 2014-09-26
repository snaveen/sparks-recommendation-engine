
import static spark.Spark.*;

import java.awt.image.DataBuffer;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import spark.Request;
public class DealsServer {

	
	
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
			category = obj.get("category").toString();
			latLong = obj.get("location").toString();
		}
	 
		List coupons = fetchDealsFromExternalSite(category, latLong);					
		return transformer.render(coupons);
	}
	
	private static List fetchDealsFromExternalSite(String category,
			String latLong) {
		// TODO Auto-generated method stub
		
		List result = new ArrayList();
		return result;
		
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
		
		categoryData.put("category", "Food");
		categoryData.put("deviceId", "1234");
		categoryData.put("location", "123, 123");
		
		categoryDoc.put("message", categoryData);
		
		categories.insert(categoryDoc);
		
		table.insert(document);
		
		return "success";
	}
	
	public static String convertDealsToMessages(String json){
		return "";
	}
	
}
