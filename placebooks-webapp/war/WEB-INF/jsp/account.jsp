<%@ page isELIgnored="false" contentType="text/html" pageEncoding="windows-1252"%>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>

<html>
<head>
<title>PlaceBooks account</title>
</head>

<body>
<h1>Account</h1>
<hr>

<%
    // This scriptlet declares and initializes "date"
    System.out.println( "Getting current user" );
    placebooks.model.User user = placebooks.controller.UserManager.getCurrentUser();
    out.println(user.getName());
%>

</body>
</html>