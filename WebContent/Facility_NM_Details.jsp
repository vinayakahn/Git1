<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title> Facility,NM Details </title>
		<script src="facilities.js">	</script>
		<script src="staff.js">	</script>
		<link rel="stylesheet" type="text/css" href="Responsive_Style.css">
		<style type="text/css">
		.well {
		    background: none;
		    height: 320px;
		}	
		.table>thead>tr>th{text-align:center;}				
		</style>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>		
	</head>
	
	<body>
	<%@include file="home.jsp" %>
		<style>			
			table,th,td { text-align:center; border-collapse:collapse; border:1px solid black; padding:5px; width:50%};					
		</style>					
		<h3 style = "color:brown; text-align:center"> Facility Details </h3>
		<center>
			<div id="facilitydisplay"></div>			
			<script>			
				var str="<div class=\"container\"><div class=\"panel-body\"><div class=\"table-responsive\">";				  
				str+="<table class=\"table table-bordered table-hover table-striped\"><thead><tr>";		
					str+="<th> Value </th>";
					str+="<th> Text </th>"
					str+="</tr>";
					str+="</thead>";
					str+="<tbody><tr>";
					for(var i=0; i<facilities.length; i++)				
					{				
						str+="<tr>";
						str+="<td>"+facilities[i].value+"</td>";					
						str+="<td>"+facilities[i].text+"</td>";
						str+="</tr>";
					}				
					str+="</tbody></table></div></div></div>";
					str+="</center>";
					console.log(str);					
					facilitydisplay.innerHTML=str;
			</script>								
			<h3 style = "color:brown; text-align:center"> Nurse_Mentor Details </h3>
			<center>
				<div id="nmdisplay"></div>				
				<script>			
				var str2="<div class=\"container\"><div class=\"panel-body\"><div class=\"table-responsive\">";				  
				str2+="<table class=\"table table-bordered table-hover table-striped\"><thead><tr>";		
					str2+="<th> Value </th>";
					str2+="<th> Text </th>"
					str2+="</tr>";
					str2+="</thead>";
					str2+="<tbody><tr>";					
						for(var j=0; j<nms.length; j++)					
						{				
							str2+="<tr>";
							str2+="<td>"+nms[j].value+"</td>";					
							str2+="<td>"+nms[j].text+"</td>";
							str2+="</tr>";
						}
						str2+="</tbody></table></div></div></div>";
						str2+="</center>";
						console.log(str2);					
						nmdisplay.innerHTML=str2;
				</script>		
	</body>	
</html>