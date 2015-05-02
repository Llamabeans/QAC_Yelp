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
				$('#query').submit(
						function() {
							$form = $(this);
							$.post($form.attr('action'), $form.serialize(),
									function(responseJson) {
										var $ul = $('<ul>').appendTo(
												$('#somediv')); // Create HTML <ul> element and append it to HTML DOM element with ID "somediv".
										$.each(responseJson, function(index,
												item) { // Iterate over the JSON array.
											$('<li>').text(item).appendTo($ul); // Create HTML <li> element, set its text content with currently iterated item and append it to the <ul>.
										});
									});
							return false; // Prevent execution of the synchronous (default) submit action of the form.
						});
			});
</script>

</head>
<body>

	<form id="query" action="query" method="post">
		<p>
			<input name="query"> <input type="submit">
		</p>
	</form>


	<div id="somediv"></div>
</body>
</html>