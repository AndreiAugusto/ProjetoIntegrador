<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${item.titulo}"/> - Catálogo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo.css">
</head>
<body>
<header>
    <h1>Detalhes do item</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/itens">Catálogo</a>
        <a href="${pageContext.request.contextPath}/busca">Buscar</a>
        <a href="${pageContext.request.contextPath}/cadastro">Cadastrar</a>
    </nav>
</header>
<main>
    <c:if test="${param.msg == 'atualizado'}">
        <p class="sucesso">Alterações salvas.</p>
    </c:if>

    <%-- Todos os valores passam por c:out (escapa HTML/JavaScript: proteção contra XSS) --%>
    <article class="detalhe">
        <h2><c:out value="${item.titulo}"/></h2>
        <dl>
            <dt>Tipo</dt>          <dd><c:out value="${item.tipoMidia}"/></dd>
            <dt>Autor/Diretor</dt> <dd><c:out value="${empty item.autorDiretor ? '—' : item.autorDiretor}"/></dd>
            <dt>Ano</dt>           <dd><c:out value="${empty item.anoLancamento ? '—' : item.anoLancamento}"/></dd>
            <dt>Gênero</dt>        <dd><c:out value="${empty item.genero ? '—' : item.genero}"/></dd>
        </dl>
        <h3>Sinopse</h3>
        <p class="sinopse"><c:out value="${empty item.sinopse ? 'Sem sinopse cadastrada.' : item.sinopse}"/></p>
    </article>

    <div class="acoes">
        <a class="botao" href="${pageContext.request.contextPath}/editar?id=${item.id}">Editar</a>
        <%-- Exclusão só por POST, com confirmação no navegador --%>
        <form method="post" action="${pageContext.request.contextPath}/excluir"
              onsubmit="return confirm('Excluir este item do catálogo? Esta ação não pode ser desfeita.');">
            <input type="hidden" name="id" value="${item.id}">
            <button type="submit" class="perigo">Excluir</button>
        </form>
        <a class="botao secundario" href="${pageContext.request.contextPath}/itens">Voltar</a>
    </div>
</main>
</body>
</html>
