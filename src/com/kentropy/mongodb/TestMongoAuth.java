package com.kentropy.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class TestMongoAuth {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MongoCredential credential = MongoCredential.createCredential("ken-admin",
                "admin",
                "kent@#14".toCharArray());
		List<MongoCredential> list= new ArrayList<MongoCredential>();
		
		ServerAddress sa = new ServerAddress("54.162.202.102");
		list.add(credential);
		MongoClient mc= new MongoClient(sa,list);
		MongoDatabase db=mc.getDatabase("test");
	MongoCollection<Document> col=	db.getCollection("test2"); 
	//DBObject dbObject = (DBObject)JSON.parse("{message:'hello world'}");
	//col.insertOne(arg0)
	Document doc=new Document();
	doc.parse("{message:'hello world'}");
	col.insertOne(doc);
	System.out.println(doc.get("_id"));
		
	}

}
