<%@ page isELIgnored="false" contentType="text/html" pageEncoding="windows-1252"%>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html>
<head>
<title>PlaceBooks admin</title>
</head>

<body>
<h1>Administration actions</h1>
<hr>
<form action='admin/add_placebook' method='POST'>
New empty PlaceBook
	<input type='hidden' value='POINT(52.5189367988799 -4.04983520507812)' name='geometry'>
	<input type='hidden' value='stuart@tropic.org.uk' name='owner'>
	<input type="submit" value="New">
</form>

<br />
<a href="admin/debug/print_placebooks">List all PlaceBooks</a>
<br />
<a href="admin/delete/all_placebooks">Delete all PlaceBooks</a>

<br />
<form action="admin/search" method="POST">
	Search: <input type="text" name="terms">
	<input type="submit" value="Search">
</form>

<br />
<h1>Everytrail Tests</h1>
<div>
<form action='admin/test/everytrail/login' method='POST'>
	<h3>Log in</h3>
	<div>Username: <input type='text' name='username'></div>
	<div>Password: <input type='password' name='password'></div>
	<input type='submit' value='Log in'>
</form>
</div>
<div>
	<form action='admin/test/everytrail/pictures' method='POST'>
		<h3>List user's pictures</h3>
		<div>Username: <input type='text' name='username'></div>
		<div>Password: <input type='password' name='password'></div>
		<input type='submit' value='List'>
	</form>
</div>
<div>
	<form action='admin/test/everytrail/trips' method='POST'>
		<h3>List user's trips</h3>
		<div>Username: <input type='text' name='username'></div>
		<div>Password: <input type='password' name='password'></div>
		<input type='submit' value='List'>
	</form>
</div>
</body>
</html>
