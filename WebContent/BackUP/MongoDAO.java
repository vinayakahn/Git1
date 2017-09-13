package com.kentropy.mongodb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import org.bson.types.ObjectId;

/**
 * This code having getBaby(),getDateRange(),getPendingKMCInitiation(),queryBabiesByFacility(),executeQuery(),
 * getKMCInitiated(),getPendingDischarge(),getBabiesForPeriod(),getMongoClient(),Duplicate(), update().
 *
 */
public class MongoDAO 
{
	public static MongoClient mongo= null;
	public String db=null;
	public String collection=null;
	
	//constructor
	/**
	 * Constructor use to create object
	 *@param db - database name
	 *@param collection - collection name
	 */
	public MongoDAO(String db,String collection)
	{
		this.db=db;
		this.collection=collection;		
	}
	
	/**
	 * This method has query to get the babies details from collection using unique_id
	 * @param unique_id - unique_id of babies
	 * @return object of arraylist
	 */
	public  ArrayList<DBObject> getBaby(String uniqueId)
	{
		MongoDatabase db1=mongo.getDatabase(db);
		System.out.println("unique_id inside getBaby() = "+uniqueId);
		String qry="[{$project:" +
				"{facility:{$arrayElemAt: [\"$data\",0]}," +
						"baby_NoKMC:{$arrayElemAt: [\"$data\",1]}}}," +
								"{$unwind: \"$baby_NoKMC\"}," +
										"{$match:{\"baby_NoKMC.unique_id\":{$eq:\""+uniqueId+"\"}}}]";
			
		 return executeQuery(qry);			
	}
	
	/**
	 * @param json string is passed as parameter to insert a document into collection
	 */
     public void insert(String json )
	 {
		DBObject obj = (DBObject)JSON.parse(json);
	    WriteResult result = mongo.getDB(db).getCollection(collection).insert(obj);
	 }
     
     	
	/**
	 * This method has query to get the babies details from collection using unique_id
	 * @param unique_id - unique_id of babies
	 */
	public  ArrayList<DBObject> getBaby1(String uniqueId)
	{
		String str="";
		String facilityQuery="";
		String match="\"data.unique_id\":{$eq:\""+uniqueId+"\"}";
		String matchQ=	"	              {	                  $match:{"+match+facilityQuery+""+"}" +
					"}" ;
			str="["+babyQuery1+","+matchQ+                   
			      "]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}
	
	/**
	 * This method used to create a vector using start date and end date
	 * @param start - start date
	 * @param end - end date
	 */
	public Vector<Date> getDateRange(Date start,Date end)
	{
		Vector<Date> v  = new Vector<Date>();
		Calendar cal=Calendar.getInstance();
		cal.setTime(start);
		Date tmp=start;
		v.add(start);
		while(end.after(tmp))
		{
			cal.add(Calendar.DATE,1);
			tmp=cal.getTime();
			v.add(tmp);
		}
		return v;
	}
	
	String babyQuery1="{$project:{\"_id\":\"$_id\",facility:{$arrayElemAt:[\"$data\", 0]}," +
		"	              data:{$arrayElemAt:[\"$data\", 1]}}}," +
		"	              {$unwind:\"$data\"}," +
				"	              {$lookup:{" +
				"	                      from:\"kmc\"," +
				"	                      localField:\"data.unique_id\"," +
				"	                      foreignField:\"unique_id\"," +
              		"	                      as:\"comp_docs\"" +
              		"	              }}," +
              		 "{$lookup:{" +
						"	                      from:\"discharge\"," +
						"	                      localField:\"data.unique_id\"," +
						"	                      foreignField:\"unique_id\"," +
                      		"	                      as:\"discharge_docs\"" +
                      		"	              }}";
	
	/**
	 * This method has query to get the babies details filter by its DOB
	 * @param facility - facility value
	 * @param match - match query
	 */
	public  ArrayList<DBObject> babiesByDOB(String facility, String datefrom, String dateto)
	{
		String str="";
		String match="\"$and\":[{\"data.dob1\":{\"$gte\":'"+datefrom+"',\"$lte\":'"+dateto+"'}}],"
					+ "$or:[{\"facility.facility\":{\"$in\":["+facility+"]}}]";		
		
		String matchQ=	"{$match:{"+match+""+"}" +
					"}" ;
			str="["+babyQuery1+","+matchQ+",{$sort:{\"data.dob1\":1}}]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}	
	
	/**
	 * This method has match query to get the details of babies which are pending for kmc initiation
	 * @param facility - facility value
	 */
	public ArrayList<DBObject> getPendingKMCInitiation(int facility)
	{
		//return null;
		System.out.println("Facility = "+facility);
		String match = "\"discharge_docs\":{$eq:[]},\"comp_docs\":{$eq:[]}, " +
		"\"data.surveyType\":{$ne:'inborn_normal'}";		
		return queryBabiesByFacility(facility, match);	
	}
	
	/**
	 * This method has match query to get the details of babies which are pending for kmc initiation
	 * using $in operation
	 * @param facility - facility value
	 */
	public ArrayList<DBObject> getPendingKMCInitiation(String facility, String datefrom, String dateto)
	{
		//return null;
		System.out.println("Facility = "+facility);
		String match = "\"discharge_docs\":{$eq:[]},\"comp_docs\":{$eq:[]}, " +
		"\"data.surveyType\":{$ne:'inborn_normal'},"+
		"$or:[{\"facility.facility\":{\"$in\":["+facility+"]}}],"+
		"\"facility.from1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'}";
		return queryByFacilityTaluk(facility, match);	
	}
	
	/**
	 * This method has query to get the babies details by facility and match query
	 * @param facility - facility value
	 * @param match - match query
	 */
	public  ArrayList<DBObject> queryBabiesByFacility(int facility, String match)
	{
		String str="";
		String facilityQuery="";
		if(facility!=1)
		{
			facilityQuery=",\"facility.facility\":"+facility+"";
		}
		String matchQ=	"	              {	                  $match:{"+match+facilityQuery+""+"}" +
					"}" ;
			str="["+babyQuery1+","+matchQ+                   
			      "]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}
	
	/**
	 * This method has query to get the babies details by facility using $in operation
	 * @param facility - facility value
	 * @param match - match query
	 */
	public  ArrayList<DBObject> queryByFacilityTaluk(String facility, String match)
	{
		String str="";				
		String matchQ=	"{$match:{"+match+""+"}" +
					"}" ;
			str="["+babyQuery1+","+matchQ+",{$sort:{\"facility.from1\":1}}]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}
	
	/**
	 * This method has query to get the babies details by facility and match query with period sort
	 * @param facility - facility value
	 * @param match - match query
	 */
	public  ArrayList<DBObject> queryBabiesByPeriodSort(int facility, String match)
	{
		String str="";
		String facilityQuery="";
		if(facility!=1)
		{
			facilityQuery=",\"facility.facility\":"+facility+"";
		}
		String matchQ=	"	              {	                  $match:{"+match+facilityQuery+""+"}" +
					"}";
			str="["+babyQuery1+","+matchQ+",{$sort:{\"facility.from1\":1}}]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}
	
	/**
	 * This method has query to get the babies details by facility and match query
	 * @param facility - facility value
	 * @param match - match query
	 */
	public  ArrayList<DBObject> queryByLimitPerPage(int facility, String match, int limit, int skip)
	{
		String str="";
		String facilityQuery="";
		if(facility!=1)
		{
			facilityQuery=",\"facility.facility\":"+facility+"";
		}
		String matchQ=	"{$match:{"+match+facilityQuery+""+"}" +
					"}" ;
			str="["+babyQuery1+","+matchQ+",{$skip:"+skip+"},{$limit:"+limit+"}" +                  
			      "]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}
	
	/**
	 * This method has query to get the babies details by facility and match query
	 * @param facility - facility value
	 * @param match - match query
	 */
	public  ArrayList<DBObject> queryByLimitPerPage(String facility, String match, int limit, int skip)
	{
		String str="";		
		String matchQ=	"{$match:{"+match+""+"}" +
					"}" ;
			str="["+babyQuery1+","+matchQ+",{$sort:{\"facility.from1\":1}},{$skip:"+skip+"},{$limit:"+limit+"}" +                  
			      "]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}
	
	/**
	 * This method has query to get the babies details by facility and match query
	 * @param facility - facility value
	 * @param match - match query
	 */
	/*public  ArrayList<DBObject> queryByLimitPerPagesort(int facility, String match, int limit, int skip)
	{
		String str="";
		String facilityQuery="";
		if(facility!=1)
		{
			facilityQuery=",\"facility.facility\":"+facility+"";
		}
		String matchQ=	"{$match:{"+match+facilityQuery+""+"}" +
					"}" ;
			str="["+babyQuery1+","+matchQ+",{$sort:{\"discharge_docs.outcome_date1\":1}},{$skip:"+skip+"},{$limit:"+limit+"}" +                  
			      "]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}*/
    
	/**
	 * This method use to execute queries
	 * @param str - Passing query
	 */
    public ArrayList<DBObject> executeQuery(String str)
    {
    	System.out.println("str inside execute query = "+str);	
    	
    	java.util.List<DBObject> list = (java.util.List<DBObject>)JSON.parse(str);	
    	//System.out.println("List size in mongo execute query= "+list.size());
    	Iterable<DBObject> output = mongo.getDB(db).getCollection(collection).aggregate(list).results(); 
    	//System.out.println("output in mongo execute query= "+output);
	    ArrayList<DBObject> jsonlist = new ArrayList<DBObject>();
	    try
	    {	    	
		   for(DBObject dbobj: output)
		   {	    	  
		    	//System.out.println("data at mongodao = "+dbobj);
		    	jsonlist.add(dbobj);
		    }	      
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace(); 
	    }	    
	    return jsonlist;
	}
   
	//kmc initiation
    /**
	 * This method has query to get the details which are initiated for kmc
	 * @param facility - facility value
	 */
	public ArrayList<DBObject> getKMCInitiated(int facility)
	{
		System.out.println("Facility in mongoDAO page= "+facility);
		String match = "\"discharge_docs\":{$eq:[]},\"comp_docs\":{$ne:[]}, "
				+ "\"comp_docs.init_date1\":{$exists:true}," +
		"\"data.surveyType\":{$ne:'inborn_normal'}";		
		
		return queryBabiesByFacility(facility, match);
	}	
		
	 /**
	 *
	 * This method has match query to get the details which are pending for discharge
	 * @param facility - facility value
	 */
	public ArrayList<DBObject> getPendingDischarge(int facility)
	{
		System.out.println("Facility = "+facility);
		String match = "\"discharge_docs\":{$eq:[]}, " +
		"\"data.surveyType\":{$ne:'inborn_normal'}";
		return queryBabiesByFacility(facility, match);
	}
	
	/**
	 * This method has match query to get the details of babies which are pending for kmc initiation and 
	 * discharged without entering discharge details 
	 * @param facility - facility value
	 */
	public ArrayList<DBObject> kmcDischargeRecords(int facility)
	{
		System.out.println("Facility = "+facility);
		String match = "\"discharge_docs\":{$eq:[]},"+
				"\"comp_docs\":{$ne:[]}," +
				"\"data.surveyType\":{$ne:'inborn_normal'}";
		return queryBabiesByFacility(facility, match);
	}
	
	/**
	 * This method has match query to get the details of babies which are pending for kmc initiation and
	 * with limit and skip operation	
	 * @param facility - facility value
	 * @param limit = limit of records per page
	 * @param skip = no of records to be skip
	 */
	public ArrayList<DBObject> kmcDischargeRecordsByLimit(int facility, int limit, int skip)
	{
		System.out.println("Facility = "+facility);		
		String match = "\"discharge_docs\":{$eq:[]},"+	
				"\"comp_docs\":{$ne:[]}," +
				"\"data.surveyType\":{$ne:'inborn_normal'}";
		return queryByLimitPerPage(facility, match, limit, skip);
	}	
	
	/**
	 * This method has match query to get the details of babies which are pending for kmc initiation and
	 * with limit and skip $in operation	
	 * @param facility - facility value
	 * @param limit = limit of records per page
	 * @param skip = no of records to be skip
	 */
	public ArrayList<DBObject> kmcDischargeRecordsByLimit(String facility, int limit, int skip)
	{
		System.out.println("Facility = "+facility);		
		String match = "\"discharge_docs\":{$eq:[]},"+	
				"\"comp_docs\":{$ne:[]}," +
				"$or:[{\"facility.facility\":{\"$in\":["+facility+"]}}],"+
				"\"data.surveyType\":{$ne:'inborn_normal'}";
		return queryByLimitPerPage(facility, match, limit, skip);
	}
	
	/**
	 * This method has match query for discharged babies with limit and skip operation	
	 * @param facility - facility value	 
	 * @param limit = limit of records per page
	 * @param skip = no of records to be skip
	 */
	public ArrayList<DBObject> dischargedBabiesWithLimit(int facility, int limit, int skip)
	{
		System.out.println("Facility = "+facility);		
		String match = "\"discharge_docs\":{$ne:[]},"+				
				"\"data.surveyType\":{$ne:'inborn_normal'}";
		return queryByLimitPerPage(facility, match, limit, skip);
	}
	
	/**
	 * This method has match query for discharged babies	
	 * @param facility - facility value	 
	 * @param limit = limit of records per page
	 * @param skip = no of records to be skip
	 */
	public ArrayList<DBObject> dischargedBabies(String facility, String datefrom, String dateto)
	{
		String sortby="dischDate";
		System.out.println("Facility = "+facility);		
		String match = "\"discharge_docs\":{$ne:[]},"+				
				"\"data.surveyType\":{$ne:'inborn_normal'},"+
				"$or:[{\"facility.facility\":{\"$in\":["+facility+"]}}],"+
				"\"discharge_docs.outcome_date1\":{$gte:'"+datefrom+"',$lt:'"+dateto+"'}";
		return queryBabiesByPeriodTalukSort(match,sortby);
	}
	
	/**
	 * This method has match query for discharged babies	
	 * @param facility - facility value	 
	 * @param limit = limit of records per page
	 * @param skip = no of records to be skip
	 */
	public ArrayList<DBObject> totalBabyCount(int facility, String datefrom, String dateto)
	{
		System.out.println("Facility = "+facility);		
		//String match = "\"_id\":{$gte:{'$oid':'"+obfrom+"'}, $lte:{'$oid':'"+obto+"'}},";
		String match = "\"data.dob1\":{$gte:'"+datefrom+"',$lt:'"+dateto+"'}";
				//match+= "\"data.surveyType\":{$ne:'inborn_normal'}";
		return queryBabiesByFacility(facility, match);
	}
	
	/**
	 * This method has match query for discharged babies $in operation	
	 * @param facility - facility value	 
	 * @param limit = limit of records per page
	 * @param skip = no of records to be skip
	 */
	public ArrayList<DBObject> totalBabyCount(String facility, String datefrom, String dateto)
	{
		String sortby="dob";
		System.out.println("Facility = "+facility);
		String match = "\"data.dob1\":{$gte:'"+datefrom+"',$lt:'"+dateto+"'},"+
				"$or:[{\"facility.facility\":{\"$in\":["+facility+"]}}]";
		return queryBabiesByPeriodTalukSort(match, sortby);
	}
		
	/**
	 * This method has match query based on TodayDate-7, TodayDate-28 and DischargeDate for kmc tracking
	 * by keeping DOB as reference
	 * @param facility - facility value
	 * @param kmcPeriod = DOB of baby
	 **/
	public ArrayList<DBObject> getBabiesForPeriod(String facility,int kmcPeriod)
	{		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String tc_7=sdf.format(cal.getTime());
		cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-28);
		String tc_28=sdf.format(cal.getTime());
		System.out.println(tc_28);
		System.out.println(tc_7);
		String periodFilter="";
		String[] periodFilters= {"","",",\"data.dob1\":{$lte:'"+tc_7+"',$gt:'"+tc_28+"'},$or:[{\"discharge_docs.outcome_date1\":{$gt:'"+tc_7+"'}},{\"discharge_docs.outcome_date1\":{$exists:false}}]",//1st option
				",\"discharge_docs.outcome_date1\":{$lte:'"+tc_7+"'},\"data.dob1\":{$gt:'"+tc_28+"'}",//2nd option
				",\"data.dob1\":{$lte:'"+tc_28+"'}"//3rd option
				};
		System.out.println("Facility = "+facility);
		String match = " " +
		"\"data.surveyType\":{$ne:'inborn_normal'}, \"comp_docs.kmc_reg_no\":{$exists:true}"+periodFilters[kmcPeriod]
				+ ",\"facility.facility\":{\"$in\":["+facility+"]}";
		return queryByFacilityTaluk(facility, match);
	}
	
	/**
	 * This method has match query based on TodayDate-7, TodayDate-28 and DischargeDate for kmc tracking with limit and skip
	 * by keeping DOB as reference
	 * @param facility - facility value
	 * @param kmcPeriod = DOB of baby
	 */
	public ArrayList<DBObject> getBabiesForPeriodCC(int facility,int kmcPeriod,int limit, int skip)
	{
		
		System.out.println("Facility = "+facility+"KMCPeriod="+kmcPeriod+"Limit="+limit+"skip="+skip);
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String tc_7=sdf.format(cal.getTime());
		cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-28);
		String tc_28=sdf.format(cal.getTime());
		System.out.println(tc_28);
		System.out.println(tc_7);
		String periodFilter="";
		String[] periodFilters= {"","",",\"data.dob1\":{$lte:'"+tc_7+"'},\"data.dob1\":{$gt:'"+tc_28+"'},\"discharge_docs.date_of_outcome\":{$gte:'"+tc_7+"'}",
				",\"discharge_docs.date_of_outcome\":{$lte:'"+tc_7+"'},\"data.dob1\":{$gt:'"+tc_28+"'}",
				",\"data.dob1\":{$lte:'"+tc_28+"'}"
				};
		System.out.println("Facility = "+facility);
		String match = " " +
		"\"data.surveyType\":{$ne:'inborn_normal'}"+periodFilters[kmcPeriod];
		return queryByLimitPerPage(facility, match, limit, skip);
		//return queryBabiesByFacility(facility, match);
	}
	
	//mongoclient initialization
	/**
	 * This method id used for mongoclient initiation
	 */
	public static MongoClient getMongoClient()
	{
		return mongo;
	}	
	
	//duplicates methods
	/**
	 * This has query to get records by uniqueID
	 * @param uniqueId - Baby UniqueId
	 */
	public ArrayList<DBObject> getRecordByUniqueid(String uniqueId)
	{
		MongoDatabase db1=mongo.getDatabase(db);
		String uniqueIdQuery = "[{$project: {output:{$arrayElemAt: [\"$data\",1]},facility:{$arrayElemAt: [\"$data\",0]}}},"
		+ "{$unwind: \"$output\"},{$unwind: \"$facility\"},"
		   + "{$match: {\"output.unique_id\":\""+uniqueId+"\"}}]";
	
		return executeQuery(uniqueIdQuery);
	}
	
	/**
	 * This has query to get the duplicates document based on pid, mother_name, birth_weight, time_of_birth, SurveyType
	 */
	public ArrayList<DBObject> getDuplicates()
	{
		MongoDatabase db1=mongo.getDatabase(db);
		String str = "[{$project:{\"_id\":1,data:{$arrayElemAt:[\"$data\", 1]}}},"
                         +"{$unwind:\"$data\"},"
                          +"{$match:{\"data.pid1\":{$exists:true}, \"data.surveyType\":{$ne:\"inborn_normal\"}}},"
                          +"{$group:{_id:{pid:\"$data.pid1\",mother_name:\"$data.mother_name\", dob1:\"$data.dob1\",birth_weight:\"$data.birth_weight\",time_of_birth:\"$data.time_of_birth\",surveyType:\"$data.surveyType\"},"                    
                          +"count:{$sum:1}}},"
                         +"{$match:{count:{$gt:1}}}]";
                     
		return executeQuery(str);
	}
	
	/**
	 * This method use to get details of babies based on time_of_birth and birth_weight
	 * @param birth_weight - birth_weight of baby
	 * @param time_of_birth - time_of_birth of baby
	 */
	public  ArrayList<DBObject> getDetails(Object birth_weight,Object time_of_birth)
	{	
		String details = "[{$project:{\"_id\":1,data:{$arrayElemAt:[\"$data\", 1]},facility:{$arrayElemAt: [\"$data\",0]}}},"
                         +"{$unwind:\"$data\"},"
                         +"{$match: {'data.birth_weight':"+birth_weight+"}},"
                         +"{$match: {'data.time_of_birth':'"+time_of_birth+"'}}]";
		
		return executeQuery(details);		
	}
	 
	//This method is used to add status field for each duplicate document
	/**
	 * This method is used to add status field for each duplicate document
	 * @param u_id - unique_id of baby
	 */
	public void update(Object u_id)
	{
		MongoDatabase db1=mongo.getDatabase(db);		
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.append("$set", new BasicDBObject().append("data.1.$.status", 1));
		BasicDBObject searchQuery = new BasicDBObject().append("data.1.unique_id", u_id);
		WriteResult out= mongo.getDB(db).getCollection(collection).update(searchQuery, newDocument);
		System.out.println("out_data::"+out);		
	}
	
	/**
	 * @param role which selects  the user
	 * @param uname username of the user
	 * @param pass password of the user
	 * @return
	 */
	public int getUser(String role,String uname, String pass,String credentials)
	{
		try{
			MongoDatabase db1=mongo.getDatabase(db);
			BasicDBObject obj= new BasicDBObject();
			//obj.put("role", role);
			obj.put("username", uname);
			//obj.put("password", pass);
			DBCursor  out =   mongo.getDB(db).getCollection(credentials).find(obj);
			if(out.size()== 0){
				//User not found
				return -1;
			}
			else
			{
			System.out.println(out);
			while(out.hasNext()){
			DBObject dbobj = out.next();
			if(!dbobj.get("role").equals(role))
			{
				//role not matched
			    return -2;	
			}
			if(dbobj.get("role").equals(role))
			{ 
					
			  if(dbobj.get("password").equals(pass))
			  {
				   //Role & password matched
					return 2;
		      }
			  else
			  {
				  //role matched password  not matched
				  return -3;
			  }
			  
			}
			}	
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
		return 0;
	}
	
	/**
	 * This method has query to get details of kmc initiated babies 
	 * @param facility to filter by facility
	 * @param datefrom & dateto = from and to date of dob period
	 */
	public ArrayList<DBObject> kmcInitBabies(int facility, String dobFrom, String dobTo)
	{
		System.out.println("Facility in mongoDAO page= "+facility);
		String match = "\"data.surveyType\":{$ne:'inborn_normal'},comp_docs:{$ne:[]},\"data.dob1\":{$gte:\""+dobFrom+"\",$lt:\""+dobTo+"\"}";		
		return queryBabiesByFacility(facility, match);		
	}
	
	/**
	 * This method has query to get details of kmc not initiated babies 
	 * @param facility to filter by facility
	 * @param datefrom & dateto = from and to date of dob
	 */
	public ArrayList<DBObject> kmcNotInitBabies(int facility, String dobFrom, String dobTo)
	{
		System.out.println("Facility in mongoDAO page= "+facility);
		String match = "\"data.surveyType\":{$ne:'inborn_normal'},comp_docs:{$eq:[]},\"data.dob1\":{$gte:\""+dobFrom+"\",$lt:\""+dobTo+"\"}";		
		return queryBabiesByFacility(facility, match);		
	}
	
	/**
	 * This method has query to get details of duplicate babies	group by mother,father name, dob
	 */
	public ArrayList<DBObject> duplicateRecords()
	{		
		MongoDatabase db1=mongo.getDatabase(db);		
		String qry="[{$project:{data:{$arrayElemAt:[\"$data\", 1]}, facility:{$arrayElemAt:[\"$data\", 0]}}},";
				qry+="{$unwind:\"$data\"}, {$unwind:\"$facility\"},";
				qry+="{ $group: { ";
				qry+="_id: { mother: \"$data.mother_name\", dob: \"$data.dob\", father:\"$data.husband_name\"},";
				qry+="count: { $sum:  1 },";
				qry+="docs: { $push: \"$_id\" },";
				qry+="data:{$push:\"$data\"},";
				qry+="facility:{$push:\"$facility\"}";
				qry+="}},";
				qry+="{ $match: {";
				qry+="count: { $gt : 1 }, \"data.surveyType\":{$ne:\"inborn_normal\"},";
				qry+="$or:[{\"data.readmit\":{$exists:false}, \"data.group_id\":{$exists:false}}]";
				qry+="}}";
				qry+="]";
		System.out.println("query = "+qry);
		 return executeQuery(qry);		
	}
	
	/**
	 * This method has query to get details of duplicate babies	by using mother,husband name,dob
	 */
	public ArrayList<DBObject> duplicateBabies(String mothername, String fathername, String dob)
	{		
		MongoDatabase db1=mongo.getDatabase(db);		
		String qry="[{$project:{data:{$arrayElemAt:[\"$data\", 1]}, facility:{$arrayElemAt:[\"$data\", 0]}}},";
				qry+="{$unwind:\"$data\"}, {$unwind:\"$facility\"},";
				qry+="{ $match: {";
				qry+="\"data.surveyType\":{$ne:\"inborn_normal\"},";
    			qry+="\"data.mother_name\":{$eq:'"+mothername+"'},";
    			qry+="\"data.husband_name\":{$eq:'"+fathername+"'},";
    			qry+="\"data.dob\":{$eq:'"+dob+"'},";
    			qry+="}}";
    			qry+="]";
		System.out.println("query = "+qry);
		 return executeQuery(qry);		
	}
	
	/**
	 * This method has match query for babies with call success details	
	 * @param facility - facility value	 
	 * @param datefrom & dateto = from and to date of from1 period
	 */
	public ArrayList<DBObject> callSuccessBabies(int facility,String datefrom, String dateto)
	{
		System.out.println("Facility = "+facility);
		String match = "\"data.surveyType\":{$ne:'inborn_normal'},"; 
				match += "\"comp_docs.kmc_reg_no\":{$exists:true},";
				match += "\"data.dob1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'},";
				match +="\"comp_docs.visit_date\":{$exists:true}";
		return queryBabiesByPeriodSort(facility, match);
	}
	
	/**
	 * This method has match query for babies with call success details	
	 * @param facility - facility value	 
	 * @param datefrom & dateto = from and to date of from1 period
	 */
	public ArrayList<DBObject> kmcCCDetails(int facility,String datefrom, String dateto)
	{
		System.out.println("Facility = "+facility);
		String match = "\"data.surveyType\":{$ne:'inborn_normal'},"; 
				match += "\"comp_docs.kmc_reg_no\":{$exists:true},";
				match += "\"facility.from1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'},";
				match +="\"comp_docs.visit_date\":{$exists:true}";
		return queryBabiesByPeriodSort(facility, match);
	}
	
	/**
	 * This method has match query for babies with call success details	$in operation
	 * @param facility - facility value	 
	 * @param datefrom & dateto = from and to date of from1 period
	 */
	public ArrayList<DBObject> kmcCCDetails(String facility,String datefrom, String dateto)
	{
		String sortby="period";
		System.out.println("Facility = "+facility);
		String match = "\"data.surveyType\":{$ne:'inborn_normal'},"; 
				match += "\"comp_docs.kmc_reg_no\":{$exists:true},";
				match += "\"facility.from1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'},";
				match +="\"comp_docs.visit_date\":{$exists:true},"+
						"$or:[{\"facility.facility\":{\"$in\":["+facility+"]}}]";;
		return queryBabiesByPeriodTalukSort(match, sortby);
	}
	
	/**
	 * This method has match query for babies with call failure and success details	
	 * @param facility - facility value	 
	 * @param datefrom & dateto = from and to date of from1 period
	 */
	public ArrayList<DBObject> callSuccess_FailureBabies(int facility,String datefrom, String dateto)
	{
		System.out.println("Facilityin mongodao = "+facility);
		String match = "\"data.surveyType\":{$ne:'inborn_normal'},"; 
			   match += "\"comp_docs.kmc_reg_no\":{$exists:true},";
			   match += "\"data.dob1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'},";
			   match += "$or: [{\"comp_docs.status\":{$exists:true}}, {\"comp_docs.visit_date\":{$exists:true}}]";
		return queryBabiesByFacility(facility, match);
	}
	
	/**
	 * This method has match query for call attempt details 
	 * by keeping period as reference $in operation
	 * @param facility - facility value
	 * @param kmcPeriod = 7th day, 7th day after discharge, 28th day filter
	 * @param datefrom & dateto = from and to date of from1 period
	 * @param birthWeight -  filter by baby weight <2000g, 2000-2499g, >=2500
	 **/
	public ArrayList<DBObject> callAttemptDetails(String facility,String datefrom, String dateto,int kmcPeriod, int birthWeight)
	{
		String sortby="period";
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String tc_7=sdf.format(cal.getTime());
		cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-28);
		String tc_28=sdf.format(cal.getTime());
		System.out.println(tc_28);
		System.out.println(tc_7);			
		String[] birthWeightFilter= {"","",",\"data.birth_weight\":{$lt:2000}",
				",\"data.birth_weight\":{$gte:2000, $lte:2499}",
				",\"data.birth_weight\":{$gte:2500}"};
		System.out.println("Facility = "+facility);
		String match = " " +
		"\"data.surveyType\":{$ne:'inborn_normal'}, \"comp_docs.kmc_reg_no\":{$exists:true},"
		+"$or:[{\"comp_docs.status\":{$exists:true}},{\"comp_docs.visit_date\":{$exists:true}}],"	
		+"\"facility.facility\":{\"$in\":["+facility+"]},"
		+ "\"facility.from1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'},"						
		+ "\"comp_docs.kmc_period\":{$eq:'"+kmcPeriod+"'}"+birthWeightFilter[birthWeight];
		return queryBabiesByPeriodTalukSort(match, sortby);		
	}
	
	/**
	 * This method has match query for call attempt details 
	 * by keeping DOB as reference  
	 * @param facility - facility value
	 * @param kmcPeriod = 7th day, 7th day after discharge, 28th day filter
	 * @param datefrom & dateto = from and to date of from1 period
	 * @param birthWeight -  filter by baby weight <2000g, 2000-2499g, >=2500
	 **/
	public ArrayList<DBObject> callAttemptDetails(int facility,String datefrom, String dateto,int kmcPeriod, int birthWeight)
	{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String tc_7=sdf.format(cal.getTime());
		cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-28);
		String tc_28=sdf.format(cal.getTime());
		System.out.println(tc_28);
		System.out.println(tc_7);			
		String[] birthWeightFilter= {"","",",\"data.birth_weight\":{$lt:2000}",
				",\"data.birth_weight\":{$gte:2000, $lte:2499}",
				",\"data.birth_weight\":{$gte:2500}"};
		System.out.println("Facility = "+facility);
		String match = " " +
		"\"data.surveyType\":{$ne:'inborn_normal'}, \"comp_docs.kmc_reg_no\":{$exists:true},"
		+ "\"facility.from1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'},"
				+ "$or: [{\"comp_docs.status\":{$exists:true}}, {\"comp_docs.visit_date\":{$exists:true}}],"		
				+ "\"comp_docs.kmc_period\":{$eq:'"+kmcPeriod+"'}"+birthWeightFilter[birthWeight];
		return queryBabiesByPeriodSort(facility, match);		
	}
	
	/**
	 * This method has match query for get baby details filter by taluk with $in operation
	 * @param facility - facility value
	 * @param datefrom & dateto = from and to date of from1 period
	 **/
	public ArrayList<DBObject> babyFacilityTalukArray(String facility,String datefrom, String dateto)
	{
		String sortby="period";
		System.out.println("SB="+facility);
		String match = "\"facility.from1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'},"
					+ "$or:[{\"facility.facility\":{\"$in\":["+facility+"]}}]";
		return queryBabiesByPeriodTalukSort(match,sortby);	
	}
	
	/**
	* This method has query to get the babies details by match query with period sort
	* @param match - match query
	*/
	public  ArrayList<DBObject> queryBabiesByPeriodTalukSort(String match, String sortby)
	{
		String str="";
		String facilityQuery="";
		String matchQ=	"{	$match:{"+match+facilityQuery+""+"}" +
				"}";
		if(sortby.equals("period"))			
		{
			str="["+babyQuery1+","+matchQ+",{$sort:{\"facility.from1\":1}}]";
		}
		else if(sortby.equals("dischDate"))
		{
			str="["+babyQuery1+","+matchQ+",{$sort:{\"discharge_docs.outcome_date1\":1}}]";
		}
		else if(sortby.equals("dob"))
		{
			str="["+babyQuery1+","+matchQ+",{$sort:{\"data.dob1\":1}}]";
		}
		else
		{
			str="["+babyQuery1+","+matchQ+"]"; //when no sorting
		}
		System.out.println("Query = "+str);	
		return executeQuery(str);	
	}
	
	/**
	 * This method has match query based on TodayDate-7, TodayDate-28 and DischargeDate for kmc tracking
	 * by keeping DOB as reference without $in operation
	 * @param facility - facility value
	 * @param kmcPeriod = DOB of baby
	 **/
	public ArrayList<DBObject> getBabiesForPeriod(int facility,int kmcPeriod)
	{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String tc_7=sdf.format(cal.getTime());
		cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-28);
		String tc_28=sdf.format(cal.getTime());
		System.out.println(tc_28);
		System.out.println(tc_7);
		String periodFilter="";
		String[] periodFilters= {"","",",\"data.dob1\":{$lte:'"+tc_7+"',$gt:'"+tc_28+"'},$or:[{\"discharge_docs.outcome_date1\":{$gt:'"+tc_7+"'}},{\"discharge_docs.outcome_date1\":{$exists:false}}]",//1st option
				",\"discharge_docs.outcome_date1\":{$lte:'"+tc_7+"'},\"data.dob1\":{$gt:'"+tc_28+"'}",//2nd option
				",\"data.dob1\":{$lte:'"+tc_28+"'}"//3rd option
				};
		System.out.println("Facility = "+facility);
		String match = " " +
		"\"data.surveyType\":{$ne:'inborn_normal'}, \"comp_docs.kmc_reg_no\":{$exists:true}"+periodFilters[kmcPeriod];
		return queryBabiesByFacility(facility, match);
	}
	
	/**
	 * This method has match query get 28day normal babies with period sort $in operation
	 * by keeping DOB as reference
	 * @param facility - facility value
	 * @param kmcPeriod = DOB of baby
	 **/
	public ArrayList<DBObject> normal28dayBaby(String facility,String datefrom, String dateto)
	{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String tc_7=sdf.format(cal.getTime());
		cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-28);
		String tc_28=sdf.format(cal.getTime());
		System.out.println(tc_28);
		System.out.println(tc_7);
		/*String periodFilter="";
		String[] periodFilters= {"","",",\"data.dob1\":{$lte:'"+tc_7+"',$gt:'"+tc_28+"'},$or:[{\"discharge_docs.outcome_date1\":{$gt:'"+tc_7+"'}},{\"discharge_docs.outcome_date1\":{$exists:false}}]",//1st option
				",\"discharge_docs.outcome_date1\":{$lte:'"+tc_7+"'},\"data.dob1\":{$gt:'"+tc_28+"'}",//2nd option
				",\"data.dob1\":{$lte:'"+tc_28+"'}"//3rd option
				};*/
		System.out.println("Facility = "+facility);
		String match = " \"facility.from1\":{$gte:'"+datefrom+"', $lte:'"+dateto+"'}," +
						"\"data.surveyType\":{$eq:'inborn_normal'},"
						+ "\"data.dob1\":{$lte:'"+tc_28+"'},"+
						"\"comp_docs.visit_date\":{$exists:false},"+
						"\"facility.facility\":{\"$in\":["+facility+"]}";
		return queryByFacilityTaluk(facility, match);
	}
	
	/**
	 * This method has match query to get home born baby details filter by taluk and village 
	 * with $in operation
	 * @param facility - facility value
	 * @param datefrom & dateto = from and to date of from1 period
	 **/
	public ArrayList<DBObject> homeBornBabies(String taluk, String town, int birthweight)
	{
		String[] birthWeightFilter= {"", ",\"birth_weight\":{$lte:2000, $gte:1}",
				",\"birth_weight\":{$gt:2000}",
				",\"birth_weight\":{$eq:-1}"};		
		String str = null;
		
		if(taluk.equals("5"))
		{
			taluk="other";
		}
		if(town.equals("other"))
		{
			str = "[{"+
					"$match:{"+
					"\"taluk_from\":'"+taluk+"',"+
					"\"community_from\":{$eq:'"+town+"'}"+
					birthWeightFilter[birthweight]+
				"}}]";
		}
		else
		{
			if(town.equals("1"))
			{
				str = "[{"+
						"$match:{"+
						"\"taluk_from\":'"+taluk+"'"+					
						birthWeightFilter[birthweight]+
					"}}]";
			}
			else
			{
				str = "[{"+
						"$match:{"+
						"\"taluk_from\":'"+taluk+"',"+
						"\"community_from\":{$in:["+town+"]}"+
						birthWeightFilter[birthweight]+
					"}}]";
			}
		}
		return executeQuery(str);	
	}
	
	/**
	 * This method has match query to get baby details per week 
	 * with $in operation
	 * @param facility - facility value
	 * @param datefrom & dateto = from and to date of from1 period
	 **/
	public ArrayList<DBObject> generateBabydetails(String datefrom, String dateto)
	{
		String str = "[{"+
            "$project:{"+
            "facility:{$arrayElemAt:[\"$data\", 0]},"+ 
            "data:{$arrayElemAt:[\"$data\", 1]}}},"+              
             "{$unwind:\"$data\"}, "+
             "{$match:{"+
                         "\"facility.from1\":{$gte:'"+datefrom+"', $lt:'"+dateto+"'},"+
                         "\"data.surveyType\":{$ne:\"inborn_normal\"}"+
             "}},"+ 
              "{$sort:{\"facility.from1\":1}}]";
		return executeQuery(str);	
	}
	
	/**
	 * This method has query to get the babies details by match query
	 * @param match - match query
	 */
	public  ArrayList<DBObject> queryBabiesByMatch(String match, String sortby)
	{
		String str="";
		String s = sortby;
		String matchQ=	"{$match:{"+match+""+"}" +
					"}" ;	                  

		if(sortby.equals("period"))			
		{
			str="["+babyQuery1+","+matchQ+",{$sort:{\"facility.from1\":1}}]";
		}
		else if(sortby.equals("dischDate"))
		{
			str="["+babyQuery1+","+matchQ+",{$sort:{\"discharge_docs.outcome_date1\":1}}]";
		}
		else if(sortby.equals("dob"))
		{
			str="["+babyQuery1+","+matchQ+",{$sort:{\"data.dob1\":1}}]";
		}
		else
		{
			str="["+babyQuery1+","+matchQ+"]"; //when no sorting
		}
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}
	
	/**
	 * This method use to execute queries
	 * @param str - Passing query
	 */
    public ArrayList<DBObject> executeQuery(String str, String collection)
    {
    	System.out.println("str inside execute query = "+str);	
    	
    	java.util.List<DBObject> list = (java.util.List<DBObject>)JSON.parse(str);	
    	//System.out.println("List size in mongo execute query= "+list.size());
    	Iterable<DBObject> output = mongo.getDB(db).getCollection(collection).aggregate(list).results(); 
    	//System.out.println("output in mongo execute query= "+output);
	    ArrayList<DBObject> jsonlist = new ArrayList<DBObject>();
	    try
	    {	    	
		   for(DBObject dbobj: output)
		   {	    	  
		    	//System.out.println("data at mongodao = "+dbobj);
		    	jsonlist.add(dbobj);
		    }	      
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace(); 
	    }	    
	    return jsonlist;
	}
    
	/**
	 * This method has match query to get kmc baby details per week
	 * with $in operation
	 * @param facility - facility value
	 * @param datefrom & dateto = from and to date of from1 period
	 **/
	public ArrayList<DBObject> generateKMCdetails(String datefrom, String dateto)
	{
		String str = "[{"+
                "$match:{"+
                        "\"date\":{$exists:true},"+
                        "\"date1\":{$gte:'"+datefrom+"', $lte:'"+dateto+"'}"+
                "}},"+
                "{$sort:{\"date1\":1}}]";
		return executeQuery(str,"kmc");		
	}
	
	/**
	 * This method has match query to get kmc baby details per week
	 * with $in operation
	 * @param facility - facility value
	 * @param datefrom & dateto = from and to date of from1 period
	 **/
	public ArrayList<DBObject> generateKMCinit(String datefrom, String dateto)
	{		
		String str = "[{"+
                "$match:{"+
                        "\"kmc_reg_no\":{$exists:true},"+
                        "\"init_date1\":{$gte:'"+datefrom+"', $lte:'"+dateto+"'}"+
                "}},"+
                "{$sort:{\"init_date1\":1}}]";
		return executeQuery(str,"kmc");	
	}
	
	/**
	 * This method has match query to get discharge details per week
	 * with $in operation
	 * @param facility - facility value
	 * @param datefrom & dateto = from and to date of from1 period
	 **/
	public ArrayList<DBObject> generateDischargedetails(String datefrom, String dateto)
	{
		String str = "[{"+
		    "$match:{"+
		        "\"date_of_outcome\":{$exists:true},"+
		        "\"outcome_date1\":{$gte:'"+datefrom+"', $lte:'"+dateto+"'}"+
		    "}},"+
		    "{$sort:{\"outcome_date1\":1}}]";
		    return executeQuery(str,"discharge");
	}	
	
	/**
	 * This method has match query to get csv data
	 * with $in operation
	 * @param facility - facility value
	 * @param datefrom & dateto = from and to date of from1 period
	 **/
	public ArrayList<DBObject> csvdata(String datefrom, String dateto)
	{
		String str = "[{$match:{"+
                                "\"datefrom\":{$gte:'"+datefrom+"'},"+
                                "\"dateto\":{$lte:'"+dateto+"'}"+
                    "}}]";
		return executeQuery(str);	
	}
	
	/**
	 * This method has query to get the babies details by facility and match query	
	 * @param match - match query
	 */
	public  ArrayList<DBObject> queryBabiesByFacility(String match)
	{
		String str="";		
		String matchQ=	"{$match:{"+match+"}}";
			str="["+babyQuery1+","+matchQ+                   
			      "]";
			System.out.println("Query = "+str);					
			return executeQuery(str);	
	}	
	
	/*
	 * List of babies entered a day
	 */
	public ArrayList<DBObject> listOfBabiesEnteredOneday(Date startDt, String facility)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String startdate = sdf.format(startDt);
		System.out.println("starte date ="+startdate);
		//calculate end date
		Calendar cal= Calendar.getInstance();
		cal.setTime(startDt);
		cal.add(Calendar.DATE, 1);
		Date endDate= cal.getTime();
		String enddate = sdf.format(endDate);
		System.out.println("end date ="+enddate);
		String start=new ObjectId(startDt).toHexString();//startdate to objectid
		String end=new ObjectId(endDate).toHexString();//enddate to objectid
		System.out.println("start objid ="+start);
		System.out.println("end objid ="+end);
				String str=    "[{$project:{"+
				                           "facility:{$arrayElemAt:[\"$data\", 0]},"+
				                           "data:{$arrayElemAt:[\"$data\", 1]}}},"+
				                         "{$unwind:\"$data\"},{$match:{"+
				                         "\"_id\":{$gte:{$oid:\""+start+"\"},$lt: {$oid:\""+end+"\"}},"+
				                         "\"data.surveyType\":{$ne:\"inborn_normal\"},";
				                         if(!facility.equals("all"))
				                         {
				                        	str+= "\"facility.facility\":{$in:["+facility+"]}";
				                         }				                         
				                        str+="}}]";
		System.out.println("executing listOfBabiesEnteredOneday");
					ArrayList<DBObject> l1=executeQuery(str);
					ArrayList<DBObject> l2=new ArrayList<DBObject>();
					for(DBObject doc1:l1)
					{
						//System.out.println(doc1);
						DBObject facilityarray=(DBObject)doc1.get("facility");
						DBObject data=(DBObject)doc1.get("data");	
						Object facv = facilityarray.get("facility");
						int facVal=0;
						if(facv instanceof Integer)
						{
							//System.out.println("facility in int");
							Integer fac = (Integer)facv;
							facVal=fac.intValue();
						}
						if(facv instanceof Double)
						{
							//System.out.println("facility in double");
							Double fac=(Double)facv;
							facVal=fac.intValue();
						}
						data.put("facility", facVal+"");
						//data.put("objectid",((ObjectId)doc1.get("_id")).getDate().toString());
						data.put("objectid",doc1.get("_id").toString());
						data.put("enteredDate",facilityarray.get("to_date"));
						l2.add(data);						
					}
					return l2;
	}
	
	/*
	 * List of repeating babies based on dob, mother name, father name
	 */
	public ArrayList<DBObject> listOfRepeatingBabies(Date startDt, String dob, String mother, String father)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String startdate = sdf.format(startDt);
		//System.out.println("starte date ="+startdate);
		//calculate end date
		Calendar cal= Calendar.getInstance();
		cal.setTime(startDt);
		cal.add(Calendar.DATE, 1);
		Date endDate= cal.getTime();
		String enddate = sdf.format(endDate);
		//System.out.println("end date ="+enddate);
		
		String start=new ObjectId(startDt).toHexString();//startdate to objectid
		String end=new ObjectId(endDate).toHexString();//enddate to objectid
		//System.out.println("start objid ="+start);
		//System.out.println("end objid ="+end);
		
		String str="[{$project:{"+
				        "facility:{$arrayElemAt:[\"$data\", 0]},"+
				                                    "data:{$arrayElemAt:[\"$data\", 1]}}},"+
				                         "{$unwind:\"$data\"},{$match:{"+	
				                         "\"_id\":{$lt:{$oid:'"+end+"'}},"+        
				                         "\"data.surveyType\":{$ne:\"inborn_normal\"},"+
				                         "\"data.dob\":{$eq:'"+dob+"'},"+
				                         "\"data.mother_name\":{$eq:'"+mother+"'},"+
				                         "\"data.husband_name\":{$eq:'"+father+"'}"+
				                        "}}]";
		System.out.println("executing listOfRepeatingBabies");
					ArrayList<DBObject> l1=executeQuery(str);
					ArrayList<DBObject> l2=new ArrayList<DBObject>();
					for(DBObject doc1:l1)
					{
						//System.out.println(doc1);
						DBObject facility=(DBObject)doc1.get("facility");
						DBObject data=(DBObject)doc1.get("data");	
						Object facv = facility.get("facility");
						int facVal=0;
						if(facv instanceof Integer)
						{
							//System.out.println("facility in int");
							Integer fac = (Integer)facv;
							facVal=fac.intValue();
						}
						if(facv instanceof Double)
						{
							//System.out.println("facility in double");
							Double fac=(Double)facv;
							facVal=fac.intValue();
						}											
						data.put("facility", facVal+"");
						//data.put("enteredDate",((ObjectId)doc1.get("_id")).getDate().toString());
						data.put("objectid",doc1.get("_id").toString());
						data.put("enteredDate",facility.get("to_date"));
						l2.add(data);						
					}					
					return l2;
	}
	
	/**
	 * List all unique babies of one day for redcap import
	 **/
	public ArrayList<DBObject> listOfUniqueBabies(Date startDt, String facility)
	{
		System.out.println("collection used in unique query ="+collection);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String startdate = sdf.format(startDt);
		//System.out.println("starte date ="+startdate);
		//calculate end date
		Calendar cal= Calendar.getInstance();
		cal.setTime(startDt);
		cal.add(Calendar.DATE, 1);
		Date endDate= cal.getTime();
		String enddate = sdf.format(endDate);
		//System.out.println("end date ="+enddate);		
		String start=new ObjectId(startDt).toHexString();//startdate to objectid
		String end=new ObjectId(endDate).toHexString();//enddate to objectid
		//System.out.println(start);
		//System.out.println(end);
		
		System.out.println("executing listOfUniqueBabies");
		String str = "[{$project:{\"_id\":\"$_id\",facility:{$arrayElemAt:[\"$data\", 0]},"+    	              
                           "data:{$arrayElemAt:[\"$data\", 1]}}},"+    	              
                           "{$unwind:\"$data\"},"+
                           "{$match:{\"_id\":{$gte:{$oid:'"+start+"'}, $lt:{$oid:'"+end+"'}},"+
                           		"\"data.duplicateof\":{$exists:false},"+
                           		"\"data.readmitof\":{$exists:false},"+
                           		"\"data.surveyType\":{$ne:'inborn_normal'},";
                           		if(!facility.equals("all"))
		                         {
		                        	str+= "\"facility.facility\":{$in:["+facility+"]}";
		                         }				                         
		                        str+="}}]";
			ArrayList<DBObject> l1=executeQuery(str);
			ArrayList<DBObject> l2=new ArrayList<DBObject>();
			for(DBObject doc1:l1)
			{
				//System.out.println(doc1);
				DBObject facilityarray=(DBObject)doc1.get("facility");
				DBObject data=(DBObject)doc1.get("data");	
				Object facv = facilityarray.get("facility");
				int facVal=0;
				if(facv instanceof Integer)
				{
					//System.out.println("facility in int");
					Integer fac = (Integer)facv;
					facVal=fac.intValue();
				}
				if(facv instanceof Double)
				{
					//System.out.println("facility in double");
					Double fac=(Double)facv;
					facVal=fac.intValue();
				}
				data.put("facility", facVal+"");
				//data.put("enteredDate",((ObjectId)doc1.get("_id")).getDate().toString());
				//data.put("objectid",doc1.get("_id").toString());
				data.put("enteredDate",facilityarray.get("to_date"));
				l2.add(data);						
			}
			System.out.println("total unique babies ="+l2.size());
			return l2;	
	}
	public static MongoDAO initMongodao(String host, String db, String user, String pwd,String coll)
	{
		// TODO Auto-generated method stub
				mongo=new MongoClient("35.154.204.175", 27017);
				List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		    	credentials.add(
		    	    MongoCredential.createScramSha1Credential(
		    	        "ken-admin",
		    	        user,
		    	        pwd.toCharArray()
		    	    )
		    	);
		    	ServerAddress sa = new ServerAddress(host);
		    	 mongo = new MongoClient( sa, credentials );
				MongoDAO mdao=new MongoDAO(db, coll);
				mdao.mongo=mongo;	
				return mdao;
	}
	
	/**
	 * This method has match query get cc details of 28day normal babies with period sort $in operation
	 * by keeping DOB as reference
	 * @param facility - facility value
	 * @param kmcPeriod = DOB of baby
	 **/
	public ArrayList<DBObject> monitoringNormalBaby(String facility,String datefrom, String dateto)
	{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String tc_7=sdf.format(cal.getTime());
		cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-28);
		String tc_28=sdf.format(cal.getTime());
		System.out.println(tc_28);
		System.out.println(tc_7);
		/*String periodFilter="";
		String[] periodFilters= {"","",",\"data.dob1\":{$lte:'"+tc_7+"',$gt:'"+tc_28+"'},$or:[{\"discharge_docs.outcome_date1\":{$gt:'"+tc_7+"'}},{\"discharge_docs.outcome_date1\":{$exists:false}}]",//1st option
				",\"discharge_docs.outcome_date1\":{$lte:'"+tc_7+"'},\"data.dob1\":{$gt:'"+tc_28+"'}",//2nd option
				",\"data.dob1\":{$lte:'"+tc_28+"'}"//3rd option
				};*/
		System.out.println("Facility = "+facility);
		String match = " \"facility.from1\":{$gte:'"+datefrom+"', $lte:'"+dateto+"'}," +
						"\"data.surveyType\":{$eq:'inborn_normal'},"
						+ "\"data.dob1\":{$lte:'"+tc_28+"'},"+
						"\"comp_docs.visit_date\":{$exists:true},"+
						"\"comp_docs.surveyType\":{$eq:'normal_cc'},"+
						"\"facility.facility\":{\"$in\":["+facility+"]}";
		return queryByFacilityTaluk(facility, match);
	}
	
	//main metod
	public static void main(String[] args) 
	{
		MongoDAO mdao = MongoDAO.initMongodao("35.154.204.175","copy","admin","kent@#14","redcap_test");
		//System.out.println(mdao.listOfBabiesEnteredOneday(new Date("08/12/2017"), "100").size());
		//System.out.println(mdao.listOfBabiesEnteredOneday(new Date("08/12/2017"), "100").get(0));//stage 1 valid date
		//System.out.println(mdao.listOfBabiesEnteredOneday(new Date(),"100").size());//stage 1 date=null(today date)
		//System.out.println(mdao.listOfBabiesEnteredOneday(new Date("08-20-20"),"100").size());//stage 1 date=null(today date)		
		
		//System.out.println(mdao.listOfRepeatingBabies(new Date("08/14/2017"),"23/7/2017", "Pushpa", "Devappa").size());
		//System.out.println(mdao.listOfRepeatingBabies(new Date("08/14/2017"),"23/7/2017", "Pushpa", "Devappa").get(0).get("unique_id"));
		//System.out.println(mdao.listOfRepeatingBabies(new Date("08/14/2017"),"23/7/2017", "Pushpa", "Devappa").get(1).get("unique_id"));
		//System.out.println(mdao.listOfRepeatingBabies("10/8/2017", "Tanjum", "Babusab").get(0).get("unique_id"));//stage 2 valid dob, mothername, fathername
		//System.out.println(mdao.listOfRepeatingBabies("10/8/2017", "Tanjum", "Babusab").get(1).get("unique_id"));
		//System.out.println(mdao.listOfRepeatingBabies("10/8/2017", "Tanjum", "Babusab").get(1));
		//System.out.println(mdao.listOfRepeatingBabies("23/7/2017", "Renuka", "Neelappa").get(1).get("unique_id"));
		//System.out.println(mdao.listOfRepeatingBabies("23/7/2017", "", "").size());//valid dob, null mother and father name			
		//System.out.println(mdao.listOfRepeatingBabies("23/7/2017", "nv123", "Neelappa").size());//valid dob, invalid morther and valid father
		//System.out.println(mdao.listOfRepeatingBabies("23/7/2017", "Renuka", "nv123").size());//valid dob, valid morther and invalid father
		//System.out.println(mdao.listOfRepeatingBabies("", "", ""));//valid dob, null mother and father name
		
		//System.out.println(mdao.listOfUniqueBabies(new Date("07/24/2017"),"100").size());	
	}
}
