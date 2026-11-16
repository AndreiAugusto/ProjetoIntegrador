package br.edu.pit.catalogo.web;

import br.edu.pit.catalogo.dao.ItemMidiaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Controlador da exclusão ({@code POST /excluir}).
 *
 * <p>Só aceita POST: um link GET poderia ser disparado sem querer (por exemplo, por
 * pré-carregamento do navegador) e apagar dados. A tela de detalhes pede confirmação
 * antes de enviar o formulário.</p>
 */
@WebServlet("/excluir")
public class ExcluirItemServlet extends HttpServlet {

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Optional<Integer> id = Requisicoes.lerId(req);
        String msg;
        if (id.isEmpty()) {
            msg = "naoencontrado";
        } else {
            try {
                msg = dao.excluir(id.get()) ? "excluido" : "naoencontrado";
            } catch (SQLException e) {
                log("Erro ao excluir o item " + id.get(), e);
                msg = "erro";
            }
        }
        resp.sendRedirect(req.getContextPath() + "/itens?msg=" + msg);
    }
}
