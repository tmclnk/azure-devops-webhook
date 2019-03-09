<%@ page import="com.prairiegrade.webhook.util.WebhookUtils" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<c:set var="req" value="${pageContext.request}" />
<c:set var="baseUrl" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />
<html>
<head>
<meta charset="ISO-8859-1">
</head>
<body>
	<table>
		<tr>
			<td>
				GET
			</td>
			<td>
				<a href="serviceportalv1/email/test">${baseUrl}/serviceportalv1/email/test</a>
			</td>
		</tr>
		<tr>
			<td>
				POST
			</td>
			<td>
				${baseUrl}/serviceportalv1/email/{supportTeam}/onCreate
			</td>
		</tr>
	</table>
	
	<%= WebhookUtils.getVersionString(request.getServletContext()) %>
</body>
</html>