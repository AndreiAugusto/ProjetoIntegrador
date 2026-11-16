<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  Formulário único para CADASTRO e EDIÇÃO.
  - Cadastro: aberto por /cadastro, sem o atributo "edicao".
  - Edição:   aberto por /editar?id=N, com os atributos "edicao" e "item".
  Depois de um envio com erro (POST), os campos mostram o que o usuário digitou (param.*);
  na primeira abertura da edição (GET), mostram os dados atuais do item.
--%>
<c:set var="enviado" value="${pageContext.request.method eq 'POST'}"/>
<c:set var="tituloPagina" value="${edicao ? 'Editar item' : 'Cadastrar item'}"/>
<c:set var="tipoAtual" value="${enviado ? param.tipoMidia : item.tipoMidia}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${tituloPagina}"/> - Catálogo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilo.css">
</head>
<body>
<header>
    <h1><c:out value="${tituloPagina}"/></h1>
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

    <form method="post" class="cadastro"
          action="${pageContext.request.contextPath}${edicao ? '/editar' : '/cadastro'}">
        <c:if test="${edicao}">
            <input type="hidden" name="id" value="<c:out value='${enviado ? param.id : item.id}'/>">
        </c:if>
        <label>Título*
            <input type="text" name="titulo" maxlength="255" required
                   value="<c:out value='${enviado ? param.titulo : item.titulo}'/>"></label>
        <label>Autor/Diretor
            <input type="text" name="autorDiretor" maxlength="255"
                   value="<c:out value='${enviado ? param.autorDiretor : item.autorDiretor}'/>"></label>
        <label>Ano
            <input type="number" name="anoLancamento" min="1000" max="2100"
                   value="<c:out value='${enviado ? param.anoLancamento : item.anoLancamento}'/>"></label>
        <label>Gênero
            <input type="text" name="genero" maxlength="100"
                   value="<c:out value='${enviado ? param.genero : item.genero}'/>"></label>
        <label>Tipo*
            <select name="tipoMidia" required>
                <option value="Livro" ${tipoAtual == 'Livro' ? 'selected' : ''}>Livro</option>
                <option value="Filme" ${tipoAtual == 'Filme' ? 'selected' : ''}>Filme</option>
                <option value="Série" ${tipoAtual == 'Série' ? 'selected' : ''}>Série</option>
            </select></label>
        <label>Sinopse
            <textarea name="sinopse" rows="4" maxlength="2000"><c:out value="${enviado ? param.sinopse : item.sinopse}"/></textarea></label>
        <div class="acoes">
            <button type="submit">${edicao ? 'Salvar alterações' : 'Salvar'}</button>
            <c:choose>
                <c:when test="${edicao}">
                    <a class="botao secundario"
                       href="${pageContext.request.contextPath}/item?id=<c:out value='${enviado ? param.id : item.id}'/>">Cancelar</a>
                </c:when>
                <c:otherwise>
                    <a class="botao secundario" href="${pageContext.request.contextPath}/itens">Cancelar</a>
                </c:otherwise>
            </c:choose>
        </div>
    </form>
</main>
</body>
</html>
