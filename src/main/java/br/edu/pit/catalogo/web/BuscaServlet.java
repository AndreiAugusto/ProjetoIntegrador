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
import java.util.List;

/**
 * Controlador da busca por título ou autor/diretor.
 *
 * <p>Parâmetro {@code modo}: {@code segura} (padrão, usa PreparedStatement) ou
 * {@code insegura} (concatena o texto no SQL — só para a demonstração da
 * Situação-problema 1).</p>
 */
@WebServlet("/busca")
public class BuscaServlet extends HttpServlet {

    private static final int TAMANHO_MAXIMO_TERMO = 100;

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String termo = req.getParameter("termo");
        boolean insegura = "insegura".equals(req.getParameter("modo"));
        req.setAttribute("termo", termo);
        req.setAttribute("modo", insegura ? "insegura" : "segura");

        if (termo != null) {
            termo = termo.trim();
            // Validação de entrada: vazio e tamanho máximo
            if (termo.isEmpty()) {
                req.setAttribute("erro", "Digite um título ou autor/diretor para buscar.");
            } else if (termo.length() > TAMANHO_MAXIMO_TERMO) {
                req.setAttribute("erro", "A busca aceita no máximo " + TAMANHO_MAXIMO_TERMO + " caracteres.");
            } else {
                buscar(req, termo, insegura);
            }
        }
        req.getRequestDispatcher("/WEB-INF/jsp/busca.jsp").forward(req, resp);
    }

    private void buscar(HttpServletRequest req, String termo, boolean insegura) {
        try {
            List<ItemMidia> resultado = insegura ? dao.buscarInseguro(termo) : dao.buscarSeguro(termo);
            req.setAttribute("resultado", resultado);
        } catch (SQLException e) {
            // Registra o detalhe no log do servidor e mostra uma mensagem genérica ao usuário
            log("Erro na busca (modo " + (insegura ? "insegura" : "segura") + ")", e);
            req.setAttribute("erro", "Não foi possível realizar a busca.");
        }
    }
}
