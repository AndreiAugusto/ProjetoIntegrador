package br.edu.pit.catalogo.dao;

import br.edu.pit.catalogo.model.ItemMidia;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Demonstra a Situação-problema 1: a mesma entrada maliciosa ataca a busca
 * concatenada e é neutralizada pela busca com PreparedStatement.
 * Usa um banco H2 em memória com os 5 itens de exemplo.
 */
class SqlInjectionTest {

    private static final String ATAQUE_OR = "' OR 1=1 --";
    private static final String ATAQUE_UNION =
            "' UNION SELECT id, login, senha_hash, 0, 'x', 'x', 'x' FROM usuario --";

    private final ItemMidiaDAO dao = new ItemMidiaDAO();

    @BeforeAll
    static void prepararBanco() throws SQLException {
        System.setProperty("catalogo.db.url", "jdbc:h2:mem:teste;DB_CLOSE_DELAY=-1");
        FabricaDeConexoes.inicializarBanco();
    }

    @Test
    @DisplayName("Busca segura funciona normalmente para um termo legítimo")
    void buscaSeguraEncontraPorAutor() throws SQLException {
        List<ItemMidia> resultado = dao.buscarSeguro("machado");
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Busca vulnerável: ' OR 1=1 -- devolve o catálogo inteiro")
    void buscaInseguraComOrDevolveTudo() throws SQLException {
        List<ItemMidia> resultado = dao.buscarInseguro(ATAQUE_OR);
        assertEquals(dao.listarTodos().size(), resultado.size());
    }

    @Test
    @DisplayName("Busca vulnerável: UNION vaza dados da tabela usuario")
    void buscaInseguraComUnionVazaUsuarios() throws SQLException {
        List<ItemMidia> resultado = dao.buscarInseguro(ATAQUE_UNION);
        assertTrue(resultado.stream().anyMatch(i -> "admin".equals(i.getTitulo())),
                "o login 'admin' apareceu como se fosse um título");
    }

    @Test
    @DisplayName("Busca segura: os mesmos ataques viram texto comum e não retornam nada")
    void buscaSeguraNeutralizaAtaques() throws SQLException {
        assertTrue(dao.buscarSeguro(ATAQUE_OR).isEmpty());
        assertTrue(dao.buscarSeguro(ATAQUE_UNION).isEmpty());
    }
}
