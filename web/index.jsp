<%@ page import="lm.charviewer.CharacterViewer" %>
<%@ page import="java.io.*,java.util.*,java.sql.*"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>


<sql:setDataSource var="snapshot" driver="com.mysql.jdbc.Driver"
                   url="jdbc:mysql://localhost:3306/wowcharacter"
                   user="root"  password="test"/>

<sql:query dataSource="${snapshot}" var="result">
    SELECT * from `character`;
</sql:query>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<link rel="stylesheet" href="static/css/characterViewerStyle.css">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

<!-- Latest compiled and minified JavaScript -->
<script src="https://code.jquery.com/jquery-3.1.1.min.js" integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8=" crossorigin="anonymous"></script>
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
                <form name="searchForm" method="POST" action="${pageContext.request.contextPath}/search">
                    <input id="characterName" type="text" name="characterName" class="form-control" placeholder="Character Name"><br>
                    <input id="realmName" type="text" name="realmName" class="form-control" placeholder="Realm Name"><br>
                    <button id="searchButton" type="submit" class="btn btn-primary" style="width: 100%">Submit</button><br>
                </form>
                <div id="error" align="center" class="alert alert-danger alert-dismissible" style="display: none">
                    <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    <strong>Error: </strong> Character not found.
                </div>
            </div>
        </div>

        <c:set var="characterViewer" value="${char}"/>

        <div id="holder" class="jumbotron mainJumbo" style="display: none">
            <div class="container" style="width: 100%">
                <div class="row" style="width: 100%">
                    <div id="statsJumbo" class="col-md-6">
                        <div class="jumbotron innerJumbo">
                            <div class="row">
                                <div class="row">
                                    <div id="curHeader" class="col-md-12">
                                        <h2 align="center" style="color: white">
                                            ${curName}
                                        </h2>
                                        <h3 align="center" style="color: white">
                                            <em>lv:${curLevel} | ${curHealth}hp</em>
                                        </h3>
                                    </div>
                                    <div id=compHeader class="col-md-3" style="display: none">
                                        <h2 align="center" style="color: white">
                                            ${compName}
                                        </h2>
                                        <h3 align="center" style="color: white">
                                            <em>lv:${compLevel} | ${compHealth}hp</em>
                                        </h3>
                                    </div>
                                    <div id="filler" class="col-md-6" style="display: none"></div>

                                </div>
                                <div class="row">
                                <div class="col-md-4">
                                    <h3 style="color: white">Attributes</h3>
                                    <table class="table table-borderless">
                                        <tbody id="attributes">
                                        </tbody>
                                    </table>

                                    <h3 style="color: white">Attack</h3>
                                    <table class="table table-borderless">
                                        <tbody id="attack"></tbody>
                                    </table>
                                </div>
                                <div class="col-md-4">
                                    <h3 style="color: white">Defense</h3>
                                    <table class="table table-borderless">
                                        <tbody id="defense"></tbody>
                                    </table>

                                    <h3 style="color: white">Enhancements</h3>
                                    <table class="table table-borderless">
                                        <tbody id="enhancements"></tbody>
                                    </table>
                                </div>
                                <div id="compareDiv" class="col-md-4">
                                    <h3 style="color: white">Compare Characters</h3>
                                    <table class="table table-borderless">
                                        <thead>
                                            <tr>
                                                <td class="tableVal">Character</td>
                                                <td class="tableVal">Level</td>
                                                <td class="tableVal">Health</td>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="row" items="${result.rows}">
                                                <tr class="compareRow" >
                                                    <td class="tableVal compareName" style="cursor: hand"><b><u><c:out value="${row.CHARNAME}"/></u></b></td>
                                                    <td class="tableKey">lv <c:out value="${row.LEVEL}"/></td>
                                                    <td class="tableKey"><c:out value="${row.HP}hp"/></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="itemsJumbo" class="col-md-5">
                        <div class="jumbotron innerJumbo">
                            <h3 style="color: white">Items</h3>
                            <table class="table table-borderless">
                                <thead>
                                    <tr>
                                        <td class="tableVal"><b>Slot</b></td>
                                        <td class="tableVal"><b>Item</b></td>
                                        <td class="tableVal"><b>Buy Price</b></td>
                                        <td class="tableVal"><b>Sell Price</b></td>
                                    </tr>
                                </thead>
                                <tbody id="items"></tbody>
                            </table>
                        </div>
                    </div>

                    <div class="col-md-1" align="right">
                        <div style="margin-right: 20px; margin-top: 20px; margin-bottom: 20px">
                            <img id="close" src="static/images/close.png" style="cursor: hand" height="50px" width="50px">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>

    <script>
        <c:if test="${notFound}">
            $("#error").slideDown();
        </c:if>

        $("#close").click(function () {
           $("#holder").slideUp();
        });

        $.extend(
            {
                redirectPost: function(location, args)
                {
                    var form = '';
                    $.each( args, function( key, value ) {
                        form += '<input type="hidden" name="'+key+'" value="'+value+'">';
                    });
                    $('<form action="'+location+'" method="POST">'+form+'</form>').appendTo('body').submit();
                }
            });


        $(function() {
            $(".compareName").on("click",function(e) {
                var redirect = "${pageContext.request.contextPath}/compare";
                $.redirectPost(redirect, {
                    compName: $(this).text(),
                    curName: "${curName}"
                });
            });
        });

        <c:if test="${compared != null}">
            $("#itemsJumbo").hide();
            $("#statsJumbo").attr('class', 'col-md-12');
            $("#curHeader").attr('class', 'col-md-3');
            $("#compHeader").show();
            $("#filler").show();

            <c:set var="compAttributes" value="${compAttributes}"/>
            <c:set var="curAttributes" value="${curAttributes}"/>
            var attributes = ${curAttributes};
            var compAttr = ${compAttributes};
            var a = "";
            for (var attr in attributes) {

                a +=    "<tr> " +
                    "<td class=\"tableKey\">" + attr + "</td>" +
                    "<td class=\"tableVal\">" + attributes[attr] +" | "+ compAttr[attr] + "</td> " +
                    "</tr>";
            }
            $("#attributes").html(a);

            <c:set var="compAttack" value="${compAttack}"/>
            <c:set var="curAttack" value="${curAttack}"/>
            var attackList = ${curAttack};
            var compAtta = ${compAttack};
            var att = "";
            for (var attack in attackList) {
                att +=    "<tr> " +
                    "<td class=\"tableKey\">" + attack + "</td> " +
                    "<td class=\"tableVal\">" + attackList[attack] +" | "+ compAtta[attack] + "</td> " +
                    "</tr>";
            }
            $("#attack").html(att);

            <c:set var="compDefense" value="${compDefense}"/>
            <c:set var="curDefense" value="${curDefense}"/>
            var defense = ${curDefense};
            var compDef = ${compDefense};
            var d = "";
            for (var def in defense) {
                d +=    "<tr> " +
                    "<td class=\"tableKey\">" + def + "</td> " +
                    "<td class=\"tableVal\">" + defense[def] +" | " + compDef[def] + "</td> " +
                    "</tr>";
            }
            $("#defense").html(d);

            <c:set var="compEnhancements" value="${compEnhancements}"/>
            <c:set var="curEnhancements" value="${curEnhancements}"/>
            var enhancements = ${curEnhancements};
            var compEnhance = ${compEnhancements};

            var e = "";
            for (var enh in enhancements) {
                e +=    "<tr> " +
                    "<td class=\"tableKey\">" + enh + "</td> " +
                    "<td class=\"tableVal\">" + enhancements[enh] +" | "+ compEnhance[enh] + "</td> " +
                    "</tr>";
            }
            $("#enhancements").html(e);
            $("#holder").slideDown();
        </c:if>

        <c:if test="${searched != null}">
            var attributes = ${characterViewer.getAttributes()};

            var a = "";
            for (var attr in attributes) {
                a +=    "<tr> " +
                            "<td class=\"tableKey\">" + attr + "</td> " +
                            "<td class=\"tableVal\">" + attributes[attr] + "</td> " +
                            "</tr>";
            }
            $("#attributes").html(a);

            var attackList = ${characterViewer.getAttack()};
            var att = "";
            for (var attack in attackList) {
                att +=    "<tr> " +
                    "<td class=\"tableKey\">" + attack + "</td> " +
                    "<td class=\"tableVal\">" + attackList[attack] + "</td> " +
                    "</tr>";
            }
            $("#attack").html(att);

            var defense = ${characterViewer.getDefense()};
            var d = "";
            for (var def in defense) {
                d +=    "<tr> " +
                    "<td class=\"tableKey\">" + def + "</td> " +
                    "<td class=\"tableVal\">" + defense[def] + "</td> " +
                    "</tr>";
            }
            $("#defense").html(d);

            var enhancements = ${characterViewer.getEnhancements()};
            var e = "";
            for (var enh in enhancements) {
                e +=    "<tr> " +
                    "<td class=\"tableKey\">" + enh + "</td> " +
                    "<td class=\"tableVal\">" + enhancements[enh] + "</td> " +
                    "</tr>";
            }
            $("#enhancements").html(e);

            var items = ${characterViewer.getItems()};
            <%--var itemList = ${itemList};--%>
            var i = "";
            items.forEach(function(entry){
//                var parsed = JSON.parse(itemList[count]);
                i +=    "<tr> " +
                            "<td class=\"tableKey\">" + entry.slot + "</td> " +
                            "<td class=\"tableKey\">" + entry.item + "</td> " +
                            "<td class=\"tableVal\">" + entry.buy + "</td> " +
                            "<td class=\"tableVal\">" + entry.sell + "</td> " +
                        "</tr>";
            });
            $("#items").html(i);
            $("#holder").slideDown();
        </c:if>

    </script>

</html>
