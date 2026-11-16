package br.edu.pit.catalogo.web;

import br.edu.pit.catalogo.dao.ItemMidiaDAO;
import br.edu.pit.catalogo.model.ItemMidia;
import br.edu.pit.catalogo.servico.ValidadorItemMidia;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static br.edu.pit.catalogo.web.Requisicoes.texto;

/**
 * Controlador da edição ({@code /editar?id=N}).
 *
 * <p>GET carrega o item e abre o mesmo formulário do cadastro já preenchido.
 * POST valida os dados com as mesmas regras do cadastro e grava com UPDATE.</p>
 */
@WebServlet("/editar")
public class EditarItemServlet extends HttpServlet {

    private static final String FORMULARIO = "/WEB-INF/jsp/cadastro.jsp";

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

    /** Abre o formulário preenchido com os dados atuais do item. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Optional<Integer> id = Requisicoes.lerId(req);
        if (id.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/itens?msg=naoencontrado");
            return;
        }
        try {
            Optional<ItemMidia> item = dao.buscarPorId(id.get());
            if (item.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/itens?msg=naoencontrado");
                return;
            }
            req.setAttribute("item", item.get());
            req.setAttribute("edicao", true);
            req.getRequestDispatcher(FORMULARIO).forward(req, resp);
        } catch (SQLException e) {
            log("Erro ao carregar o item " + id.get() + " para edição", e);
            resp.sendRedirect(req.getContextPath() + "/itens?msg=erro");
        }
    }

    /** Valida e grava as alterações. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Optional<Integer> id = Requisicoes.lerId(req);
        if (id.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/itens?msg=naoencontrado");
            return;
        }

        ValidadorItemMidia.Resultado validacao = ValidadorItemMidia.validar(
                texto(req, "titulo"), texto(req, "autorDiretor"), texto(req, "anoLancamento"),
                texto(req, "genero"), texto(req, "sinopse"), texto(req, "tipoMidia"));
        ItemMidia item = validacao.item();
        item.setId(id.get());
        req.setAttribute("item", item);
        req.setAttribute("edicao", true);

        if (!validacao.valido()) {
            req.setAttribute("erros", validacao.erros());
            req.getRequestDispatcher(FORMULARIO).forward(req, resp);
            return;
        }

        try {
            if (dao.atualizar(item)) {
                resp.sendRedirect(req.getContextPath() + "/item?id=" + item.getId() + "&msg=atualizado");
            } else {
                // O item foi excluído por outra pessoa/aba enquanto o formulário estava aberto
                resp.sendRedirect(req.getContextPath() + "/itens?msg=naoencontrado");
            }
        } catch (SQLException e) {
            log("Erro ao atualizar o item " + item.getId(), e);
            req.setAttribute("erros", List.of("Não foi possível salvar as alterações. Tente novamente."));
            req.getRequestDispatcher(FORMULARIO).forward(req, resp);
        }
    }
}
