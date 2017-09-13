<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">

  <div class="container-fluid">
    <div class="navbar-header">

    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>                        
      </button>
      <a class="navbar-brand" href="#"><span class="label label-danger">KMC Project Report</span></a>
    </div>
<div class="collapse navbar-collapse" id="myNavbar">
    <ul class="nav navbar-nav">
      
      <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">FI Forms<span class="caret"></span></a>
        <ul class="dropdown-menu">
         <li> <a href="test1_1.html" target="listing">Facility new born baby listing form </a><br></li>
          <li><a href="KMC_Pending_Details.jsp" target="listing">Enter the KMC initiation date</a><br></li>
          <li><a href="KMC_Initiated_Records.jsp" target="listing">KMC initiated babies(Enter KMC details) </a></li>
        </ul>
      </li>

	<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Call Center Forms<span class="caret"></span></a>
        <ul class="dropdown-menu">
          <li><a href="kmc_tracking.jsp" target="listing">Track KMC </a><br></li>
        </ul>
      </li>

        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Data Reports<span class="caret"></span></a>
        <ul class="dropdown-menu">
          <li><a href="allDocuments.jsp" target="report1">All listing  Documents</a><br></li>
        <li><a href="BabyDetails_by_facility_date.jsp" target="report1">New born listing report</a><br></li>
        <li><a href="BabyDetails_by_dob.jsp" target="report1">New born tabular listing report</a><br></li>
         <li><a href="Facility_NM_Details.jsp" target="report2" > Facility and nurse mentor(FI) details</a><br></li>
         <li><a href="searchCW.jsp" target="report1">Community Workers</a><br><li>
        </ul>
      </li>
	<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Error Reports<span class="caret"></span></a>
        <ul class="dropdown-menu">
          <li><a href="ErrorReport.jsp" target="error"> Extra Records</a><br></li>
          <li><a href="Each-Field-Report.jsp" target="error">Identifier Contact details Coverage</a><br></li>
          <li><a href="LBW_error_report.jsp" target="error">Mistmatch in lbw count and data collected</a></li>
        </ul>
      </li>
     
    </ul>

<ul class="nav navbar-nav navbar-right">
      <li><a href="#"><span class="glyphicon glyphicon-user"></span> Sign Up</a></li>
      <li><a href="login.jsp"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
    </ul>
  </div>
</nav><br>
<br>
<br>


</body>
</html>