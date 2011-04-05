<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
<title>List PlaceBooks</title>
</head>


<body>
<h1>Listing PlaceBooks...</h1>
<br />
<c:forEach items="${pbs}" var="pb">
	<div style='border:2px dashed;padding:5px'><b>
	PlaceBook: ${pb.key}, owner=${pb.owner.email}, timestamp=${pb.timestamp}, 
	geometry=${pb.geometry}, ??? elements</b>
	[<a href='../package/${pb.key}'>package</a>] 
	[<a href='../delete/${pb.key}'>delete</a>] 
	[<a href='../shelf/${pb.owner.email}'>shelf for this user</a>]
	<form action='../add_metadata' method='POST'>
		Add metadata:
		<input type='hidden' name='key' value='${pb.key}'> 
		<input type='text' name='mKey'>
		<input type='text' name='mValue'>
		<input type='submit' value='Add'>
	</form>
	<form action='../add_item/upload' method='POST' enctype='multipart/form-data'>
		Upload video: 
		<input type='file' name='video.${pb.key}'>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'>
		<input type='hidden' value='http://www.test.com' name='sourceurl'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Upload'>
	</form>
	<form action='../add_item/upload' method='POST' enctype='multipart/form-data'>
		Upload audio: 
		<input type='file' name='audio.${pb.key}'>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'>
		<input type='hidden' value='http://www.test.com' name='sourceurl'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Upload'>
	</form>
	<form action='../add_item/webbundle' method='POST'>
		Web scrape: 
		<input type='text' name='url.${pb.key}'>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Scrape'>
	</form>
	<form action='../add_item/text' method='POST'>
		Text: <input type='text' name='text.${pb.key}'>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Upload'>
	</form>
	<form action='../add_item/upload' method='POST' enctype='multipart/form-data'>
		Upload image: 
		<input type='file' name='image.${pb.key}'>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'>
		<input type='hidden' value='http://www.test.com' name='sourceurl'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Upload'>
	</form>
	<form action='../add_item/upload' method='POST' enctype='multipart/form-data'>
		Upload GPS trace: 
		<input type='file' name='gpstrace.${pb.key}'>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='hidden' value='http://www.everytrail.com' name='sourceurl'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Upload'>
	</form>
	
	<c:forEach var="item" items="${pb.items}">
		<div style='border:1px dotted;padding:5px'>
		${item.class}: ${item.key}, owner=${item.owner.email}, 
		timestamp=${item.timestamp}
		</div>
	</c:forEach>
	</div><br/>
</c:forEach>
</body>
</html>
