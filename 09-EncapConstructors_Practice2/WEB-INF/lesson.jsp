<%-- 
    Document   : lesson
    Created on : Nov 5, 2013, 10:24:14 PM
    Author     : mheimer
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <c:choose>
            <c:when test="${isSolution}">
                <title>Solution: ${chapterName}, ${exerciseName}</title>
            </c:when>
            <c:otherwise>
                <title>Exercise: ${chapterName}, ${exerciseName}</title>
            </c:otherwise>
        </c:choose>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="../../css/jquery-ui/flick/jquery-ui-1.10.3.custom.css" rel="stylesheet"/>
    <link href="../../css/codemirror/codemirror.css" rel="stylesheet"/>
    <link href="../../css/codemirror/theme/eclipse.css" rel="stylesheet"/>
    <link href="../../css/cloud-ide/ide.css" rel="stylesheet"/>
    <script src="../../js/codemirror/codemirror.js"></script>
    <script src="../../js/codemirror/clike.js"></script>
    <script src="../../js/jquery/jquery-1.9.1.js"></script>
    <script src="../../js/jquery-ui/jquery-ui-1.10.3.custom.js"></script>
    <script src="../../js/jquery/jquery.cookie.js"></script>
    <script src="../../js/cloud-ide/jquery.stcurr.ide.js"></script>
    <script>
        $(document).ready(function() {
            $(".ide").ide({
                tabs: ${tabs},
                URL_PREFIX : "../../"
            });
        });
    </script>
</head>
<body>
    <c:choose>
        <c:when test="${isSolution}">
            <div>Solution: ${chapterName}, ${exerciseName} | <a href='../../lessons/index.html'>Index</a> | <a href='../../lessons/${chapterName}/${exerciseName}'>Exercise</a></div>
        </c:when>
        <c:otherwise>
            <div>Exercise: ${chapterName}, ${exerciseName} | <a href='../../lessons/index.html'>Index</a> | <a href='../../lesson-solutions/${chapterName}/${exerciseName}'>Solution</a></div>
        </c:otherwise>
    </c:choose>
    <div class="ide">Press the New button to begin.</div>
    ${readme}
</body>
</html>
