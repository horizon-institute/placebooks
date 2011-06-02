<%@page import="placebooks.controller.EMFSingleton"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="placebooks.controller.UserManager"%>
<%@page import="placebooks.model.User"%>
<%@page import="placebooks.model.PlaceBook"%>
<%@ page isELIgnored="false" contentType="text/html" pageEncoding="windows-1252"%>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>

<html>
<head>
<title>PlaceBooks account</title>
</head>

<body>
<h1>Account</h1>
<%
    // This scriptlet declares and initializes "date"
    System.out.println( "Getting current user" );
	EntityManager pm = EMFSingleton.getEntityManager();
    User user = UserManager.getCurrentUser(pm);
    out.println("<p>" + user.getName() + "</p>");
    out.println("<p>" + user.getEmail() + "</p>");
    
    for(PlaceBook placebook: user.getPlacebooks())
    {
    	out.println("<a href=\"\">" + placebook.getKey() + "</a>");
    }
%>
<a href="/placebooks/addEverytrail.html">Add Everytrail Login</a>

</body>
</html>