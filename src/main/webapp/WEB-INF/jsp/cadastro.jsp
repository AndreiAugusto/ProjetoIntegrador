<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Cadastrar - Catálogo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo.css">
</head>
<body>
<header>
    <h1>Cadastrar item</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/itens">Catálogo</a>
        <a href="${pageContext.request.contextPath}/busca">Buscar</a>
        <a href="${pageContext.request.contextPath}/cadastro">Cadastrar</a>
    </nav>
</header>
<main>
    <c:if test="${not empty erros}">
        <ul class="erro">
            <c:forEach var="e" items="${erros}"><li><c:out value="${e}"/></li></c:forEach>
        </ul>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/cadastro" class="cadastro">
        <label>Título*
            <input type="text" name="titulo" maxlength="255" required value="<c:out value='${param.titulo}'/>"></label>
        <label>Autor/Diretor
            <input type="text" name="autorDiretor" maxlength="255" value="<c:out value='${param.autorDiretor}'/>"></label>
        <label>Ano
            <input type="number" name="anoLancamento" min="1000" max="2100" value="<c:out value='${param.anoLancamento}'/>"></label>
        <label>Gênero
            <input type="text" name="genero" maxlength="100" value="<c:out value='${param.genero}'/>"></label>
        <label>Tipo*
            <select name="tipoMidia" required>
                <option value="Livro" ${param.tipoMidia == 'Livro' ? 'selected' : ''}>Livro</option>
                <option value="Filme" ${param.tipoMidia == 'Filme' ? 'selected' : ''}>Filme</option>
                <option value="Série" ${param.tipoMidia == 'Série' ? 'selected' : ''}>Série</option>
            </select></label>
        <label>Sinopse
            <textarea name="sinopse" rows="4"><c:out value="${param.sinopse}"/></textarea></label>
        <button type="submit">Salvar</button>
    </form>
</main>
</body>
</html>
