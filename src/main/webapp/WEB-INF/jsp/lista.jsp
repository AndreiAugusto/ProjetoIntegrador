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
    <%-- Mensagens após redirecionamento. O texto é fixo: o valor de "msg" nunca é exibido. --%>
    <c:choose>
        <c:when test="${param.msg == 'cadastrado'}"><p class="sucesso">Item cadastrado.</p></c:when>
        <c:when test="${param.msg == 'excluido'}"><p class="sucesso">Item excluído do catálogo.</p></c:when>
        <c:when test="${param.msg == 'naoencontrado'}"><p class="erro">Item não encontrado.</p></c:when>
        <c:when test="${param.msg == 'erro'}"><p class="erro">Não foi possível concluir a operação. Tente novamente.</p></c:when>
    </c:choose>

    <c:if test="${not empty erro}">
        <p class="erro"><c:out value="${erro}"/></p>
    </c:if>

    <table>
        <thead>
        <tr><th>Título</th><th>Autor/Diretor</th><th>Ano</th><th>Gênero</th><th>Tipo</th><th>Ações</th></tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${itens}">
            <tr>
                <%-- c:out escapa HTML/JavaScript digitado pelo usuário (proteção contra XSS) --%>
                <td><a href="${pageContext.request.contextPath}/item?id=${item.id}"><c:out value="${item.titulo}"/></a></td>
                <td><c:out value="${item.autorDiretor}"/></td>
                <td><c:out value="${item.anoLancamento}"/></td>
                <td><c:out value="${item.genero}"/></td>
                <td><c:out value="${item.tipoMidia}"/></td>
                <td class="acoes-tabela">
                    <a href="${pageContext.request.contextPath}/item?id=${item.id}">Ver</a>
                    <a href="${pageContext.request.contextPath}/editar?id=${item.id}">Editar</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty itens and empty erro}">
            <tr><td colspan="6">Nenhum item cadastrado.</td></tr>
        </c:if>
        </tbody>
    </table>
</main>
</body>
</html>
