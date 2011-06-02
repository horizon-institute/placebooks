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
	[<a href='../delete_placebook/${pb.key}'>delete</a>] 
	[<a href='../shelf/${pb.owner.email}'>shelf for this user</a>]
	<form action='../add_placebook_metadata' method='POST'>
		Add metadata:
		<input type='hidden' name='key' value='${pb.key}'> 
		key (String): <input type='text' name='mKey'>
		value (String): <input type='text' name='mValue'>
		<input type='submit' value='Add'>
	</form>

	<form action='../add_item/map' method='POST'>
		Generate mapitem
		<input type='hidden' name='map.${pb.key}' value=''>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='text' value='POLYGON ((52.651864 1.261797, 52.651864 1.322222, 52.607322 1.322222, 52.607322 1.261797, 52.651864 1.261797))' name='geometry'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Gen map'>
	</form>

	<form action='../add_item/uploadandcreate' method='POST' enctype='multipart/form-data'>
		Upload video: 
		<input type='file' name='video.${pb.key}'>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'>
		<input type='hidden' value='http://www.test.com' name='sourceurl'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Upload'>
	</form>
	<form action='../add_item/uploadandcreate' method='POST' enctype='multipart/form-data'>
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
	<form action='../add_item/uploadandcreate' method='POST' enctype='multipart/form-data'>
		Upload image: 
		<input type='file' name='image.${pb.key}'>
		<input type='hidden' name='key' value='${pb.key}'>
		<input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'>
		<input type='hidden' value='http://www.test.com' name='sourceurl'>
		<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
		<input type='submit' value='Upload'>
	</form>
	<form action='../add_item/uploadandcreate' method='POST' enctype='multipart/form-data'>
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
		[<a href='../delete_placebookitem/${item.key}'>delete</a>]
		
		<form action='../add_placebookitem_mapping/metadata' method='POST'>
			Add metadata:
			<input type='hidden' name='key' value='${item.key}'> 
			key (String): <input type='text' name='mKey'>
			value (String): <input type='text' name='mValue'>
			<input type='submit' value='Add'>
		</form><form action='../add_placebookitem_mapping/parameter' method='POST'>
			Add parameter:
			<input type='hidden' name='key' value='${item.key}'> 
			key (String): <input type='text' name='mKey'>
			value (int): <input type='text' name='mValue'>
			<input type='submit' value='Add'>
		</form>

		</div>
	</c:forEach>
	</div><br/>
</c:forEach>
<div><a href="<%=request.getContextPath()%>/placebooks/a/admin">Return to admin page</a></div>
</body>
</html>
