<%@ page import="lm.charviewer.CharacterViewer" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>



<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<link rel="stylesheet" href="static/css/characterViewerStyle.css">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>

<html>
    <head>
        <title>Character Viewer</title>
        <link rel="shortcut icon" href="static/images/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <div align="center">
            <img src="static/images/eagledream.png">
            <div class="form-group" style="width: 20%">
                <form name="searchForm" method="POST" action="${pageContext.request.contextPath}/SearchServlet">
                        <input id="characterName" type="text" name="characterName" class="form-control" placeholder="Character Name"><br>
                        <input id="realmName" type="text" name="realmName" class="form-control" placeholder="Realm Name"><br>
                        <button type="submit" class="btn btn-primary" style="width: 100%">Submit</button><br>
                </form>
            </div>
        </div>

        <div class="jumbotron mainJumbo">
            <div class="container" style="width: 100%">
                <div class="row">
                    <div class="col-md-8">
                        <div class="jumbotron innerJumbo">
                            <div class="row">
                                <h2 align="center" style="color: white"><%=CharacterViewer.getName()%></h2>
                                <h3 align="center" style="color: white"><em>lv:<%=CharacterViewer.getLevel()%> | <%=CharacterViewer.getHealth()%>hp</em></h3>
                                <div class="col-md-4">
                                    <h3 style="color: white">Attributes</h3>
                                    <table class="table borderless">
                                        <c:forEach items="<%=CharacterViewer.getAttributes()%>" var="attribues" >
                                            <tr>
                                                <td class="tableKey">${attribues.key}</td>
                                                <td class="tableVal">${attribues.value}</td>
                                            </tr>
                                        </c:forEach>
                                    </table>

                                    <h3 style="color: white">Attack</h3>
                                    <table class="table borderless">
                                        <c:forEach items="<%=CharacterViewer.getAttack()%>" var="attack" >
                                            <tr>
                                                <td class="tableKey">${attack.key}</td>
                                                <td class="tableVal">${attack.value}</td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                                <div class="col-md-4">
                                    <h3 style="color: white">Defense</h3>
                                    <table class="table borderless">
                                        <c:forEach items="<%=CharacterViewer.getDefense()%>" var="defense" >
                                            <tr>
                                                <td class="tableKey">${defense.key}</td>
                                                <td class="tableVal">${defense.value}</td>
                                            </tr>
                                        </c:forEach>
                                    </table>

                                    <h3 style="color: white">Enhancements</h3>
                                    <table class="table borderless">
                                        <c:forEach items="<%=CharacterViewer.getEnhancements()%>" var="enhancement" >
                                            <tr>
                                                <td class="tableKey">${enhancement.key}</td>
                                                <td class="tableVal">${enhancement.value}</td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                                <div class="col-md-4">
                                    <h3 style="color: #95732e">Compare Stats</h3>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="jumbotron innerJumbo">
                            <h3 style="color: #95732e">My Item Sets</h3>
                            <table class="table borderless">
                                <c:forEach items="<%=CharacterViewer.getItems()%>" var="items" >
                                    <tr>
                                        <td class="tableKey">${items.key}</td>
                                        <td class="tableVal">${items.value}</td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
