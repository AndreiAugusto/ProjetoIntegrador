<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Catálogo de Livros e Filmes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo.css">
</head>
<body>
<header>
    <h1>Catálogo de Livros e Filmes</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/itens">Catálogo</a>
        <a href="${pageContext.request.contextPath}/busca">Buscar</a>
        <a href="${pageContext.request.contextPath}/cadastro">Cadastrar</a>
    </nav>
</header>
<main>
    <c:if test="${not empty erro}">
        <p class="erro"><c:out value="${erro}"/></p>
    </c:if>

    <table>
        <thead>
        <tr><th>Título</th><th>Autor/Diretor</th><th>Ano</th><th>Gênero</th><th>Tipo</th></tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${itens}">
            <tr>
                <%-- c:out escapa HTML/JavaScript digitado pelo usuário (proteção contra XSS) --%>
                <td><c:out value="${item.titulo}"/></td>
                <td><c:out value="${item.autorDiretor}"/></td>
                <td><c:out value="${item.anoLancamento}"/></td>
                <td><c:out value="${item.genero}"/></td>
                <td><c:out value="${item.tipoMidia}"/></td>
            </tr>
        </c:forEach>
        <c:if test="${empty itens and empty erro}">
            <tr><td colspan="5">Nenhum item cadastrado.</td></tr>
        </c:if>
        </tbody>
    </table>
</main>
</body>
</html>
