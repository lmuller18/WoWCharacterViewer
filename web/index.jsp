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
<script
        src="https://code.jquery.com/jquery-3.1.1.min.js"
        integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
        crossorigin="anonymous"></script>

<html>
    <head>
        <title>Character Viewer</title>
        <link rel="shortcut icon" href="static/images/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <div align="center">
            <img src="static/images/eagledream.png">
            <div class="form-group" style="width: 20%">
                <form name="searchForm" method="POST" action="${pageContext.request.contextPath}/search">
                    <input id="characterName" type="text" name="characterName" class="form-control" placeholder="Character Name"><br>
                    <input id="realmName" type="text" name="realmName" class="form-control" placeholder="Realm Name"><br>
                    <button id="searchButton" type="submit" class="btn btn-primary" style="width: 100%">Submit</button><br>
                </form>
            </div>
        </div>

        <div id="holder" class="jumbotron mainJumbo" style="display: none">
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
                                        <tbody id="attributes">
                                        </tbody>
                                    </table>

                                    <h3 style="color: white">Attack</h3>
                                    <table class="table borderless">
                                        <tbody id="attack"></tbody>
                                    </table>
                                </div>
                                <div class="col-md-4">
                                    <h3 style="color: white">Defense</h3>
                                    <table class="table borderless">
                                        <tbody id="defense"></tbody>
                                    </table>

                                    <h3 style="color: white">Enhancements</h3>
                                    <table class="table borderless">
                                        <tbody id="enhancements"></tbody>
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
                                <tbody id="items"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>

    <script>
        <c:if test="${searched != null}">
            var attributes = <%=CharacterViewer.getAttributes()%>;
            var a = "";
            for (var attr in attributes) {
                a +=    "<tr> " +
                            "<td class=\"tableKey\">" + attr + "</td> " +
                            "<td class=\"tableVal\">" + attributes[attr] + "</td> " +
                            "</tr>";
            }
            $("#attributes").html(a);

            var attackList = <%=CharacterViewer.getAttack()%>;
            var att = "";
            for (var attack in attackList) {
                att +=    "<tr> " +
                    "<td class=\"tableKey\">" + attack + "</td> " +
                    "<td class=\"tableVal\">" + attackList[attack] + "</td> " +
                    "</tr>";
            }
            $("#attack").html(att);

            var defense = <%=CharacterViewer.getDefense()%>;
            var d = "";
            for (var def in defense) {
                d +=    "<tr> " +
                    "<td class=\"tableKey\">" + def + "</td> " +
                    "<td class=\"tableVal\">" + defense[def] + "</td> " +
                    "</tr>";
            }
            $("#defense").html(d);

            var enhancements = <%=CharacterViewer.getEnhancements()%>;
            var e = "";
            for (var enh in enhancements) {
                e +=    "<tr> " +
                    "<td class=\"tableKey\">" + enh + "</td> " +
                    "<td class=\"tableVal\">" + enhancements[enh] + "</td> " +
                    "</tr>";
            }
            $("#enhancements").html(e);

            var items = <%=CharacterViewer.getItems()%>;
            var i = "";
            for (var it in items) {
                i +=    "<tr> " +
                    "<td class=\"tableKey\">" + it + "</td> " +
                    "<td class=\"tableVal\">" + items[it] + "</td> " +
                    "</tr>";
            }
            $("#items").html(i);


            $("#holder").slideDown();
        </c:if>

        <%--<%String searched = (String)request.getAttribute("searched");%>--%>
        <%--<%=searched%>--%>
        <%--<%if (searched != null){%>--%>
            <%--var attributes = <%=CharacterViewer.getAttributes()%>;--%>
            <%--for (var attr in attributes) {--%>
                <%--$("#attributes").html("<tr> <td> attr.key </td> <td> attr.val </td> </tr>")--%>
            <%--}--%>
            <%--$("#holder").slideDown();--%>
        <%--<%}%>--%>

    </script>

</html>
