package br.edu.pit.catalogo.dao;

import br.edu.pit.catalogo.model.ItemMidia;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de integração do CRUD do {@link ItemMidiaDAO} (inserir, buscar por id,
 * atualizar e excluir) em um banco H2 em memória separado do usado pelo
 * {@link SqlInjectionTest}.
 */
class ItemMidiaDAOCrudTest {

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

    @BeforeAll
    static void prepararBanco() throws SQLException {
        System.setProperty("catalogo.db.url", "jdbc:h2:mem:crud;DB_CLOSE_DELAY=-1");
        FabricaDeConexoes.inicializarBanco();
    }

    private static ItemMidia novoItem(String titulo) {
        return new ItemMidia(titulo, "Autor Teste", 2020, "Drama", "Sinopse de teste.", "Filme");
    }

    @Test
    @DisplayName("Inserir devolve o id gerado e buscarPorId recupera todos os campos")
    void inserirEBuscarPorId() throws SQLException {
        int id = dao.inserir(novoItem("Filme Inserido"));

        ItemMidia salvo = dao.buscarPorId(id).orElseThrow();
        assertEquals(id, salvo.getId());
        assertEquals("Filme Inserido", salvo.getTitulo());
        assertEquals("Autor Teste", salvo.getAutorDiretor());
        assertEquals(2020, salvo.getAnoLancamento());
        assertEquals("Drama", salvo.getGenero());
        assertEquals("Sinopse de teste.", salvo.getSinopse());
        assertEquals("Filme", salvo.getTipoMidia());
    }

    @Test
    @DisplayName("buscarPorId com id inexistente devolve vazio")
    void buscarPorIdInexistente() throws SQLException {
        assertTrue(dao.buscarPorId(999_999).isEmpty());
    }

    @Test
    @DisplayName("Atualizar altera os campos do item e devolve true")
    void atualizarItemExistente() throws SQLException {
        int id = dao.inserir(novoItem("Título Antigo"));

        ItemMidia alterado = new ItemMidia("Título Novo", "Outro Autor", 1999, "Comédia", "Nova sinopse.", "Série");
        alterado.setId(id);
        assertTrue(dao.atualizar(alterado));

        ItemMidia salvo = dao.buscarPorId(id).orElseThrow();
        assertEquals("Título Novo", salvo.getTitulo());
        assertEquals("Outro Autor", salvo.getAutorDiretor());
        assertEquals(1999, salvo.getAnoLancamento());
        assertEquals("Comédia", salvo.getGenero());
        assertEquals("Nova sinopse.", salvo.getSinopse());
        assertEquals("Série", salvo.getTipoMidia());
    }

    @Test
    @DisplayName("Atualizar pode apagar o ano (grava NULL)")
    void atualizarComAnoNulo() throws SQLException {
        int id = dao.inserir(novoItem("Sem Ano"));
        ItemMidia item = dao.buscarPorId(id).orElseThrow();
        item.setAnoLancamento(null);

        assertTrue(dao.atualizar(item));
        assertNull(dao.buscarPorId(id).orElseThrow().getAnoLancamento());
    }

    @Test
    @DisplayName("Atualizar um id inexistente devolve false e não cria nada")
    void atualizarInexistente() throws SQLException {
        int antes = dao.listarTodos().size();
        ItemMidia fantasma = novoItem("Fantasma");
        fantasma.setId(999_999);

        assertFalse(dao.atualizar(fantasma));
        assertEquals(antes, dao.listarTodos().size());
    }

    @Test
    @DisplayName("Atualizar sem id é recusado")
    void atualizarSemId() {
        assertThrows(IllegalArgumentException.class, () -> dao.atualizar(novoItem("Sem id")));
    }

    @Test
    @DisplayName("Excluir remove o item; excluir de novo devolve false")
    void excluir() throws SQLException {
        int id = dao.inserir(novoItem("Para Excluir"));

        assertTrue(dao.excluir(id));
        assertEquals(Optional.empty(), dao.buscarPorId(id));
        assertFalse(dao.excluir(id));
    }

    @Test
    @DisplayName("Excluir um item não afeta os demais")
    void excluirApenasUm() throws SQLException {
        int manter = dao.inserir(novoItem("Manter"));
        int remover = dao.inserir(novoItem("Remover"));
        int antes = dao.listarTodos().size();

        assertTrue(dao.excluir(remover));
        assertEquals(antes - 1, dao.listarTodos().size());
        assertTrue(dao.buscarPorId(manter).isPresent());
    }

    @Test
    @DisplayName("SQL malicioso no UPDATE é gravado como texto e não altera outras linhas")
    void atualizarComTextoMalicioso() throws SQLException {
        int alvo = dao.inserir(novoItem("Alvo"));
        int vizinho = dao.inserir(novoItem("Vizinho"));
        String ataque = "x', titulo = 'HACK' WHERE 1=1; DROP TABLE item_midia; --";

        ItemMidia item = dao.buscarPorId(alvo).orElseThrow();
        item.setTitulo(ataque);
        assertTrue(dao.atualizar(item));

        assertEquals(ataque, dao.buscarPorId(alvo).orElseThrow().getTitulo());
        assertEquals("Vizinho", dao.buscarPorId(vizinho).orElseThrow().getTitulo());
        assertFalse(dao.listarTodos().isEmpty(), "a tabela continua existindo");
    }
}
