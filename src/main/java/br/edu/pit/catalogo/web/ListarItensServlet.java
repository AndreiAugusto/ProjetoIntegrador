package br.edu.pit.catalogo.web;

import br.edu.pit.catalogo.dao.ItemMidiaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** Controlador da listagem: busca os itens no DAO e encaminha para lista.jsp. */
@WebServlet(urlPatterns = {"", "/itens"})
public class ListarItensServlet extends HttpServlet {

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("itens", dao.listarTodos());
        } catch (SQLException e) {
            log("Erro ao listar itens", e);
            req.setAttribute("erro", "Não foi possível carregar o catálogo. Tente novamente.");
        }
        req.getRequestDispatcher("/WEB-INF/jsp/lista.jsp").forward(req, resp);
    }
}
