<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:ui="http://java.sun.com/jsf/facelets">

<h:head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<!-- 
	<h:outputStylesheet library="css" name="dblog_blue.css" />
	 -->
	<title>#{headtitle}</title>
</h:head>
<h:body>
	<f:view>
		<div id="main">
			<div id="header">
				<ui:insert name="header">
					<ui:include src="/template/dblog_header.jsp" />
				</ui:insert>
			</div>
			<div id="menu">
				<ui:insert name="menu">
					<ui:include src="/template/menu.jsp" />
				</ui:insert>
			</div>
			<div id="content">
				<ui:insert name="content" />
			</div>
			<div id="footer">
				<ui:insert name="footer">
					<ui:include src="/template/footer.jsp" />
				</ui:insert>
			</div>
		</div>
	</f:view>
</h:body>
</html>