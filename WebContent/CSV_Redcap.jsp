<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
		<%@page import="java.io.File"%>
		<%@page import="java.io.FileWriter"%>
		<%@page import="java.io.IOException"%>
		<%@page import="com.csvreader.CsvWriter"%>
		<%@page import="java.sql.*" %>
		<%@page import="java.awt.List" %>
		<%@page import="com.mongodb.ServerAddress" %>
		<%@page import="com.mongodb.DBCursor" %>
		<%@page import="com.mongodb.DBObject" %>
		<%@page import="com.mongodb.BasicDBObject" %>
		<%@page import="com.mongodb.DBCollection" %>
		<%@page import="com.mongodb.DB" %>
		<%@page import="com.mongodb.BasicDBObjectBuilder" %>
		<%@page import="com.mongodb.BasicDBList" %>
		<%@page import="com.mongodb.AggregationOutput" %>
		<%@page import="com.mongodb.MongoException" %>
		<%@page import="com.mongodb.MongoClient" %>
		<%@page import="org.bson.types.ObjectId" %>
		<%@page import="com.mongodb.ServerAddress" %>
		<%@page import="java.awt.List" %>
		<%@page import="java.util.ArrayList"%>
		<%@page import="java.awt.List" %>
		<%@page import="org.json.JSONObject" %>
		<%@page import="org.json.JSONArray" %>
		<%@page import="java.text.*" %>
		<%-- <%@page import="java.time.*" %> --%>
		<%@page import = "java.util.*"%>
		<%@page import = "java.util.Date"%>
		<%@page import = "java.util.Calendar"%>	
		<%@ include file="Config.jsp" %>

<%

//String url =session.getServletContext().getRealPath("/")+"/redcapCSV/";
//String url = "D:/";
String url = "C:/Users/pp/Desktop/CSV/";
//System.out.println("Date99 = "+ cal.getTime());
String datefrom=null;
datefrom = request.getParameter("datefrom");	
String dateto=null;
datefrom = request.getParameter("datefrom");	
dateto=request.getParameter("dateto");
SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

if(datefrom == null)
{
	Date today = new Date();
	String dtfrm = sdf.format(today); 
	System.out.println("date after convert ="+dtfrm);
	datefrom = dtfrm;				
}
else
{				
	String[] datestr = datefrom.split("/");
	String newdate = datestr[1]+"/"+datestr[0]+"/"+datestr[2];
	datefrom = newdate;
}
if(dateto==null)
{
	Calendar cal= Calendar.getInstance();
	cal.setTime(new Date(datefrom));
	cal.add(Calendar.DATE, 1);
	Date endDate= cal.getTime();
	dateto = sdf.format(endDate);
}
else
{
	String[] dateend = dateto.split("/");
	String newdate1 = dateend[1]+"/"+dateend[0]+"/"+dateend[2];
	dateto=newdate1;
}
	
out.println("start date ="+datefrom);
out.println("end date ="+dateto);

try {
	
	MongoClient mongo = (MongoClient)com.kentropy.mongodb.MongoDAO.getMongoClient();
	System.out.println("mongo = "+mongo);
	DB database = mongo.getDB(db);
	System.out.println("Used db ="+database);
	System.out.println("Connected to database sucessfully...");
	DBCollection collection = database.getCollection(coll);						
	com.kentropy.mongodb.MongoDAO mongodao = new com.kentropy.mongodb.MongoDAO(db,redcap);				
	com.kentropy.kmc.bean.BabyDetails bd = new com.kentropy.kmc.bean.BabyDetails();	
	com.kentropy.kmc.bean.TimeCalculation tc = new com.kentropy.kmc.bean.TimeCalculation();
	int res=0;				
	// use FileWriter constructor that specifies
	CsvWriter csvOutput=null;
	//String filename="redcap-"+datefrom+"_To_"+dateto+".csv";
	String filename="redcap.csv";
	String outputFile=url+filename;
	out.println("<br>Output path="+outputFile);	
	 ArrayList<DBObject> jsonArray = mongodao.generateRedcapData(new Date(datefrom), new Date(dateto));
    out.println("Records="+jsonArray.size());
	if(jsonArray.size()>0)
	{
				csvOutput = new CsvWriter(new FileWriter(outputFile), ',');
		    	csvOutput.write("record_id");
				csvOutput.write("pid1");
				csvOutput.write("mother_name");
				csvOutput.write("husband_name");
				csvOutput.write("dob");
				csvOutput.write("time_of_birth");	
				csvOutput.write("birth_weight");
				csvOutput.write("sex");
				csvOutput.write("baby_status");
				csvOutput.write("phone1");
				csvOutput.write("phone2");
				csvOutput.write("thayi_card_no");
				csvOutput.write("facility");
				csvOutput.write("enteredDate");
				
			/* 	csvOutput.write("kmc_reg_no");
				csvOutput.write("kmc_initiation");	
				csvOutput.write("reason_for_dalay");
				csvOutput.write("date_of_kmc_initiation");
				csvOutput.write("time_of_kmc_initiation");
				csvOutput.write("am_pm");
				csvOutput.write("kmc_provider");
				csvOutput.write("feed_type");					
				csvOutput.write("date");
				csvOutput.write("kmc_done");
				csvOutput.write("kmc_time_slots_today");
				csvOutput.write("from");
				csvOutput.write("from_meridian");
				csvOutput.write("to");
				csvOutput.write("to_meridian");
				csvOutput.write("discharged");
				csvOutput.write("date_of_outcome");
				csvOutput.write("time_of_discharge");
				csvOutput.write("meridian");  */
				csvOutput.endRecord();
		      res=com.kentropy.mongodb.RedcapCSV.generateRedcapCSV(jsonArray, outputFile,csvOutput);
		    }
		    else
		    {
		    	out.println("<span style=color:red> No records found between"+datefrom+"to"+dateto+"</span>");
		    }
		  if(res==1)
		{
			 File file=new File(outputFile);
			 DecimalFormat df1=new DecimalFormat();
			 df1.setMaximumFractionDigits(2);
			 float size=file.length();
			 if(size>0)
			 {
				     out.println("file size="+df1.format(size/1024)+"KB");
			        int res1=new com.kentropy.mongodb.DeleteMongodbObject().insertCSVdetails(filename,datefrom,dateto,db,csv,"redcap",df1.format(size/1024));	
				    if(res1==3)
					{
						out.println("<span style=color:red>File replaced</span>");
					}
					else if(res1==1)
					{
						out.println("<span style=color:green>File created successfully</span>");
					} 
				    }
				    else
				   {
					 out.println("No records");
				  } 
		}   
	
}
catch (IOException e)
{
	out.println(e);
	e.printStackTrace();
}

%>
</body>
</html>