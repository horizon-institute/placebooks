<%@ page isELIgnored="false" contentType="text/html" pageEncoding="windows-1252"%>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>List PlaceBooks</title>
</head>


<body>
<c:forEach var="pb" items="${requestScope.pbs}" varStatus="rowCounter">
${pb.key}
</c:forEach>
</body>
</html>
