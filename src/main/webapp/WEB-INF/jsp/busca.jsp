<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Buscar - Catálogo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo.css">
</head>
<body>
<header>
    <h1>Buscar por título ou autor/diretor</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/itens">Catálogo</a>
        <a href="${pageContext.request.contextPath}/busca">Buscar</a>
        <a href="${pageContext.request.contextPath}/cadastro">Cadastrar</a>
    </nav>
</header>
<main>
    <form method="get" action="${pageContext.request.contextPath}/busca" class="busca">
        <input type="text" name="termo" maxlength="100" placeholder="Ex.: Machado"
               value="<c:out value='${termo}'/>">
        <label><input type="radio" name="modo" value="segura" ${modo != 'insegura' ? 'checked' : ''}>
            Busca segura (PreparedStatement)</label>
        <label><input type="radio" name="modo" value="insegura" ${modo == 'insegura' ? 'checked' : ''}>
            Busca vulnerável (concatenação) — só para demonstração</label>
        <button type="submit">Buscar</button>
    </form>

    <c:if test="${modo == 'insegura'}">
        <p class="aviso">Modo vulnerável ativo: o texto digitado é colado direto no SQL.
            Experimente <code>' OR 1=1 --</code></p>
    </c:if>

    <c:if test="${not empty erro}">
        <p class="erro"><c:out value="${erro}"/></p>
    </c:if>

    <c:if test="${resultado != null}">
        <p><c:out value="${resultado.size()}"/> resultado(s) para "<c:out value="${termo}"/>"</p>
        <table>
            <thead>
            <tr><th>Título</th><th>Autor/Diretor</th><th>Ano</th><th>Gênero</th><th>Tipo</th></tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${resultado}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/item?id=${item.id}"><c:out value="${item.titulo}"/></a></td>
                    <td><c:out value="${item.autorDiretor}"/></td>
                    <td><c:out value="${item.anoLancamento}"/></td>
                    <td><c:out value="${item.genero}"/></td>
                    <td><c:out value="${item.tipoMidia}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</main>
</body>
</html>
