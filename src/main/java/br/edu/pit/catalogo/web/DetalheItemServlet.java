package br.edu.pit.catalogo.web;

import br.edu.pit.catalogo.dao.ItemMidiaDAO;
import br.edu.pit.catalogo.model.ItemMidia;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Controlador da tela de detalhes ({@code /item?id=N}): mostra todos os campos de um item,
 * inclusive a sinopse, com os botões de editar e excluir.
 */
@WebServlet("/item")
public class DetalheItemServlet extends HttpServlet {

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

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
            req.getRequestDispatcher("/WEB-INF/jsp/detalhe.jsp").forward(req, resp);
        } catch (SQLException e) {
            log("Erro ao carregar o item " + id.get(), e);
            resp.sendRedirect(req.getContextPath() + "/itens?msg=erro");
        }
    }
}
