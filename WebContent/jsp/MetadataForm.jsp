<%@page import="de.fuberlin.panda.metadata.config.MetadataSourceType"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Metadata Request</title>
</head>
<body>
	Please fill in the desired information to retrieve metadata:<br />
	<hr />
	
	<form method="get" name="metadata" action="MetadataRequest">
		<table>
			<tr>
				<td>Metadata source: </td>
				<td>
					<select name="source" size="1">
   						<option><%=MetadataSourceType.XML_TEST%></option>
   						<option><%=MetadataSourceType.XML%></option>
   						<option selected><%=MetadataSourceType.VIRTUOSO%></option>
					</select>
				</td>
			</tr>
			<tr>
				<td>Metadata fusion? </td>
				<td>
					<input type="checkbox" name="fusion" value="1" checked="checked"> 
					(fusion can just be performed for 5 or more URIs)
				</td>
			</tr>
			<tr>
				<td>Requested URIs: <br/>
				(seperated by "CLRF")</td>
				<td><textarea name="uris" cols="50" rows="10"></textarea></td>
			</tr>
		</table>
		<br/>
		<input type="submit" value="request metadata" />
	</form>
</body>
</html>