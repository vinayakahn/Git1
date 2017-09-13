package com.kentropy.mongodb;

import java.io.IOException;
import java.util.ArrayList;

import com.csvreader.CsvWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author 
 *
 */
public class RedcapCSV {
	
	//baby details
		public static Object dob =" ";
		public static Object record_id = " ";
		public static Object pid1 = " ";	
		public static Object mother_name = " ";
		public static Object sex =" ";
		public static Object phone1 = " ";
		public static Object birth_weight=" ";
		public static Object husband_name =" ";
		public static Object phone2 =" ";
		public static Object surveytype = " ";
		public static Object baby_status = " ";
		public static Object time_of_birth = " ";
		public static Object thayi_card_no = " ";
		public static Object half = " ";
		public static Object facility = " ";
		public static Object community_from = " ";
		public static Object fac_name="";
		public static Object type_fac="";
		
		
		//kmc init fields
		public static Object kmc_reg_no = " ";
		public static Object kmc_initiation = " ";	
		public static Object date_of_kmc_initiation = " ";
		public static Object time_of_kmc_initiation = " ";	
		public static Object am_pm = " ";
		public static Object kmc_feed_type = " ";	
		public static Object reason = " ";
		public static Object kmc_provider = " ";
		//kmc details fields
		public static Object kmc_done = " ";
		public static Object kmc_date = " ";
		public static Object kmc_from_time = " ";
		public static Object kmc_to_time = " ";
		public static Object kmc_from_meridian = " ";
		public static Object kmc_to_meridian = " ";
		public static Object discharged = " ";	
		//discharge fields
		public static Object date_of_outcome=" ";
		public static Object time_of_discharge=" ";
		public static Object meridian=" ";
		static Object kmc_surveytype=null;
		public MongoDAO mongodao=null;
		
		
		/**
		 * @param jsonArray String
		 * @param outputFile String
		 * @param csvOutput Writer
		 * @return 
		 * @throws IOException
		 */
		public static int generateRedcapCSV(ArrayList<DBObject> jsonArray,String outputFile, CsvWriter csvOutput) throws IOException
		{
			System.out.println("jsonArray size="+jsonArray.size());
			com.kentropy.kmc.bean.TimeCalculation tc = new com.kentropy.kmc.bean.TimeCalculation();
			try
			{
				for(int i=0; i<jsonArray.size(); i++)
				{
					System.out.println("Line1="+i);
					BasicDBObject data_obj = (BasicDBObject)jsonArray.get(i);	
					if(data_obj.containsField("record_id"))
					{
						record_id = data_obj.get("record_id");
					}
					if(data_obj.containsField("pid1"))
					{
						pid1 = data_obj.get("pid1");
						if(pid1 instanceof String)
						{
							pid1 = data_obj.get("pid1");
						}
						else
						{
							pid1 = data_obj.getInt("pid1");
						}						
				
					}

					if(data_obj.containsField("time_of_birth"))
					{
						time_of_birth = data_obj.getString("time_of_birth");
					}
					else
						time_of_birth="";
					if(data_obj.containsField("half"))
					{
						half = data_obj.getString("half");
					}
					else
						half="";
					//to ckeck whether the time is in 12hrs format									
				/*	String time12hrs = tc.convert24To12Format((String)time_of_birth);
					time_of_birth = time12hrs;	*/								
							
					if(data_obj.containsField("thayi_card_no"))
					{
						thayi_card_no = data_obj.getLong("thayi_card_no");
					}	
					else
						thayi_card_no="";
					if(data_obj.containsField("baby_status"))
					{
						baby_status = data_obj.getString("baby_status");					
					}
					else
						baby_status="";

					if(data_obj.containsField("phone2"))
					{
						phone2 = data_obj.getLong("phone2");
					}
					else
						phone2="";
						
					if(data_obj.containsField("phone1"))
					{
						phone1 = data_obj.getLong("phone1");
					}
					else
						phone1="";
					if(data_obj.containsField("birth_weight"))
					{
						birth_weight = data_obj.getInt("birth_weight");
						//System.out.println("birth_weight:"+bd.birth_weight);
					}	

					if(data_obj.containsField("sex"))
					{
						sex = data_obj.getString("sex");
						//System.out.println("birth_weight:"+bd.birth_weight);
					}	
					else
						sex="";
					
					if(data_obj.containsField("surveyType"))
					{
						surveytype = data_obj.getString("surveyType");
					}
					if(surveytype.equals("homeborn_listing"))
					{
						type_fac="2";
					}
					else
					{
						type_fac="1";
					}
					
					// write out a records	
					csvOutput.write(record_id.toString());
					csvOutput.write(type_fac.toString());
					if(type_fac.equals("1"))   // If baby born in facility
					{
						csvOutput.write(data_obj.getString("facility"));
						csvOutput.write(data_obj.getString(""));
						csvOutput.write(data_obj.getString(""));
					}
					else // If baby born in home
					{
						csvOutput.write(data_obj.getString(""));
						csvOutput.write(data_obj.getString("community_from"));
						csvOutput.write(data_obj.getString("taluk_from"));
					}
			    	csvOutput.write(data_obj.getString("unique_id"));
					csvOutput.write(pid1.toString());
					csvOutput.write(data_obj.getString("mother_name"));
					csvOutput.write(data_obj.getString("husband_name"));
					csvOutput.write(data_obj.getString("dob"));
					csvOutput.write(time_of_birth.toString());
					csvOutput.write(half.toString());
					csvOutput.write(birth_weight.toString());
					csvOutput.write(sex.toString());
					csvOutput.write(baby_status.toString());
					csvOutput.write(phone1.toString());
					csvOutput.write(phone2.toString());
					csvOutput.write(thayi_card_no.toString());
					csvOutput.write(surveytype.toString());
					csvOutput.write(data_obj.getString("enteredDate"));
			//		csvOutput.write(data_obj.getString("kmc_reg_no"));
				//	csvOutput.write(data_obj.getString("date_of_outcome"));
					csvOutput.endRecord();
				
				}
				csvOutput.close();
				return 1;
			}
	catch(Exception e)
	{
		System.out.println("exception"+e);
		return 0;
	}
			
			
}
}