<!--
Copyright (C) 2013 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<%@ page import="com.appspot.ghackoutcome.AuthUtil" %>
<%@ page import="com.appspot.ghackoutcome.MainServlet" %>
<%@ page import="com.appspot.ghackoutcome.MirrorClient" %>
<%@ page import="com.appspot.ghackoutcome.WebUtil" %>
<%@ page import="com.google.api.client.auth.oauth2.Credential" %>
<%@ page import="com.google.api.services.mirror.model.Contact" %>
<%@ page import="com.google.api.services.mirror.model.Subscription" %>
<%@ page import="com.google.api.services.mirror.model.TimelineItem" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<%
    String userId = AuthUtil.getUserId(request);
    String appBaseUrl = WebUtil.buildUrl(request, "/");

    Credential credential = AuthUtil.getCredential(userId);

    Contact contact = MirrorClient.getContact(credential, MainServlet.CONTACT_ID);

    List<TimelineItem> timelineItems = MirrorClient.listItems(credential, 3L).getItems();


    List<Subscription> subscriptions = MirrorClient.listSubscriptions(credential).getItems();
    boolean timelineSubscriptionExists = false;
    boolean locationSubscriptionExists = false;


    if (subscriptions != null) {
        for (Subscription subscription : subscriptions) {
            if (subscription.getId().equals("timeline")) {
                timelineSubscriptionExists = true;
            }
            if (subscription.getId().equals("locations")) {
                locationSubscriptionExists = true;
            }
        }
    }

%>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OUTCOME</title>
    <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet"
          media="screen">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css"
          rel="stylesheet" media="screen">
    <link href="/static/main.css" rel="stylesheet" media="screen">
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand text-center" href="#"><img class="img-responsive" src="http://static.squarespace.com/static/53a5bdede4b0cd7da76bbacb/t/53a5ce85e4b0b6428d0d13e0/1403383346236/?format=1500w"/></a>
        </div>
    </div>
</div>
<div class="container">
    <% String flash = WebUtil.getClearFlash(request);

        if (flash != null) { %>
    <div class="alert alert-info"><%= StringEscapeUtils.escapeHtml4(flash) %>
    </div>
    <% } %>
    <div class="row">
    <div class="row">
        <p class="lead">
            Please select a participant to work with:
        </p>
    </div>
        <% AuthUtil.getParticipants(request); %>
        
       
        <c:forEach var="item" items="${participantsListTest}">
            <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
                <input type="hidden" name="operation" value="insertParticipant">
                <input type="hidden" name="pid" value="${item.firstName}-${item.lastName}">

                <p>
                    <button class="btn btn-primary btn-lg btn-block" type="submit">
                            ${item.firstName} ${item.lastName}</button>
                </p>
            </form>
        </c:forEach>

    </div>
</div>
</body>
</html>
