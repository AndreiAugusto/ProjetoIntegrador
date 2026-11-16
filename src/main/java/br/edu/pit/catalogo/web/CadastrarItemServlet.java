package br.edu.pit.catalogo.web;

import br.edu.pit.catalogo.dao.ItemMidiaDAO;
import br.edu.pit.catalogo.servico.ValidadorItemMidia;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static br.edu.pit.catalogo.web.Requisicoes.texto;

/** Controlador do cadastro: valida o formulário e grava o item pelo DAO. */
@WebServlet("/cadastro")
public class CadastrarItemServlet extends HttpServlet {

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

    /** Exibe o formulário vazio. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/cadastro.jsp").forward(req, resp);
    }

    /** Valida os dados enviados e, se estiverem corretos, insere o item. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        ValidadorItemMidia.Resultado validacao = ValidadorItemMidia.validar(
                texto(req, "titulo"), texto(req, "autorDiretor"), texto(req, "anoLancamento"),
                texto(req, "genero"), texto(req, "sinopse"), texto(req, "tipoMidia"));

        if (!validacao.valido()) {
            req.setAttribute("erros", validacao.erros());
            req.getRequestDispatcher("/WEB-INF/jsp/cadastro.jsp").forward(req, resp);
            return;
        }

        try {
            dao.inserir(validacao.item());
            resp.sendRedirect(req.getContextPath() + "/itens?msg=cadastrado");
        } catch (SQLException e) {
            log("Erro ao cadastrar item", e);
            req.setAttribute("erros", List.of("Não foi possível salvar o item. Tente novamente."));
            req.getRequestDispatcher("/WEB-INF/jsp/cadastro.jsp").forward(req, resp);
        }
    }
}
