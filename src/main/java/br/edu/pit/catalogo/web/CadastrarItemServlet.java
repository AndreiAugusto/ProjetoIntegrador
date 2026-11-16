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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Controlador do cadastro: valida o formulário e grava o item pelo DAO. */
@WebServlet("/cadastro")
public class CadastrarItemServlet extends HttpServlet {

    private static final Set<String> TIPOS = Set.of("Livro", "Filme", "Série");

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/cadastro.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        List<String> erros = new ArrayList<>();

        String titulo = texto(req, "titulo");
        String autor = texto(req, "autorDiretor");
        String anoTexto = texto(req, "anoLancamento");
        String genero = texto(req, "genero");
        String sinopse = texto(req, "sinopse");
        String tipo = texto(req, "tipoMidia");

        if (titulo.isEmpty()) erros.add("O título é obrigatório.");
        if (titulo.length() > 255) erros.add("O título deve ter no máximo 255 caracteres.");
        if (autor.length() > 255) erros.add("O autor/diretor deve ter no máximo 255 caracteres.");
        if (genero.length() > 100) erros.add("O gênero deve ter no máximo 100 caracteres.");
        if (!TIPOS.contains(tipo)) erros.add("Escolha o tipo: Livro, Filme ou Série.");

        Integer ano = null;
        if (!anoTexto.isEmpty()) {
            try {
                ano = Integer.parseInt(anoTexto);
                if (ano < 1000 || ano > 2100) erros.add("Informe um ano entre 1000 e 2100.");
            } catch (NumberFormatException e) {
                erros.add("O ano deve ser um número.");
            }
        }

        if (!erros.isEmpty()) {
            req.setAttribute("erros", erros);
            req.getRequestDispatcher("/WEB-INF/jsp/cadastro.jsp").forward(req, resp);
            return;
        }

        try {
            dao.inserir(new ItemMidia(titulo, autor, ano, genero, sinopse, tipo));
            resp.sendRedirect(req.getContextPath() + "/itens");
        } catch (SQLException e) {
            log("Erro ao cadastrar item", e);
            req.setAttribute("erros", List.of("Não foi possível salvar o item. Tente novamente."));
            req.getRequestDispatcher("/WEB-INF/jsp/cadastro.jsp").forward(req, resp);
        }
    }

    private static String texto(HttpServletRequest req, String nome) {
        String valor = req.getParameter(nome);
        return valor == null ? "" : valor.trim();
    }
}
