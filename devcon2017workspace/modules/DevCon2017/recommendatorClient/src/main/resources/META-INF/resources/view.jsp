<%@ include file="/init.jsp" %>

<p>
	<!--  <b><liferay-ui:message key="recommendatorClient.caption"/></b> -->
	<%
		Map<String,String> urls=((Map<String,String>)request.getAttribute("urls"));
		for(String k:urls.keySet()){
			%><br /><a href="<%=urls.get(k)%>"><%=k%></a><%
		}
	%>
</p>