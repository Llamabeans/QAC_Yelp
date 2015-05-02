<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Jason's QAC!</title>
<link href="css/bootstrap.min.css" rel="stylesheet" />
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script>
	$(document).ready(
			function() {
				// Button id
				$("#query").submit(
						function() {
							// Servlet name
							$form = $(this);
							$.post($form.attr('action'), $form.serialize(),
									function(responseText) {
										$('#somediv').text(responseText);
									});
							return false;
						});
			});
</script>
</head>
<body>

	<form id="query" action="AutoComplete" method="post">
		<p>
			<input name="input"><input type="submit">
		</p>
	</form>


	<div id="somediv"></div>
</body>
</html>