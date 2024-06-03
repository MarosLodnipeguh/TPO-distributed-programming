<%--
  Created by IntelliJ IDEA.
  User: maREEE
  Date: 1.6.24
  Time: 15:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Books</title>
</head>
<body>
<h1>Books</h1>
<form method="get" action="books">
    <input type="text" name="title" placeholder="Szukaj"/>
    <input type="submit" value="Search"/>
</form>
<table border="1">
    <tr>
        <th>ISBN</th>
        <th>Title</th>
        <th>Author</th>
        <th>Publisher</th>
        <th>Year</th>
        <th>Price</th>
    </tr>
    <c:forEach var="book" items="${books}">
        <tr>
            <td>${book.isbn}</td>
            <td>${book.tytul}</td>
            <td>${book.autor.name}</td>
            <td>${book.wydawca.name}</td>
            <td>${book.rok}</td>
            <td>${book.cena}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>

