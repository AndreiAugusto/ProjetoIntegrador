package br.edu.pit.catalogo.dao;

import br.edu.pit.catalogo.model.ItemMidia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) da entidade {@link ItemMidia}.
 * Concentra todo o SQL da aplicação, separando o acesso a dados das Servlets.
 */
public class ItemMidiaDAO {

    private static final String COLUNAS =
            "id, titulo, autor_diretor, ano_lancamento, genero, sinopse, tipo_midia";

    /**
     * Lista todos os itens do catálogo, ordenados por título.
     *
     * @return lista de itens (vazia se não houver nenhum)
     * @throws SQLException em caso de erro no banco
     */
    public List<ItemMidia> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM item_midia ORDER BY titulo";
        try (Connection conn = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return mapear(rs);
        }
    }

    /**
     * Insere um novo item usando PreparedStatement.
     *
     * @param item item já validado
     * @throws SQLException em caso de erro no banco
     */
    public void inserir(ItemMidia item) throws SQLException {
        String sql = "INSERT INTO item_midia (titulo, autor_diretor, ano_lancamento, genero, sinopse, tipo_midia) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getTitulo());
            stmt.setString(2, item.getAutorDiretor());
            if (item.getAnoLancamento() == null) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, item.getAnoLancamento());
            }
            stmt.setString(4, item.getGenero());
            stmt.setString(5, item.getSinopse());
            stmt.setString(6, item.getTipoMidia());
            stmt.executeUpdate();
        }
    }

    /**
     * VERSÃO VULNERÁVEL — mantida APENAS para demonstrar a Situação-problema 1.
     *
     * <p>O termo digitado pelo usuário é concatenado direto no SQL. Assim, um texto como
     * {@code ' OR 1=1 --} vira parte do comando e muda o que a consulta faz.
     * Nunca use este padrão em um sistema real.</p>
     *
     * @param termo texto digitado na busca
     * @return itens encontrados
     * @throws SQLException em caso de erro no banco (inclusive SQL quebrado pelo ataque)
     */
    public List<ItemMidia> buscarInseguro(String termo) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM item_midia "
                + "WHERE titulo LIKE '%" + termo + "%' OR autor_diretor LIKE '%" + termo + "%' "
                + "ORDER BY titulo";
        try (Connection conn = FabricaDeConexoes.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return mapear(rs);
        }
    }

    /**
     * VERSÃO CORRIGIDA — busca por título ou autor/diretor com PreparedStatement.
     *
     * <p>O SQL é pré-compilado com marcadores {@code ?} e o termo é enviado separadamente
     * como dado. Por isso, aspas, {@code OR}, {@code UNION} ou {@code --} digitados pelo
     * usuário são tratados apenas como texto a ser procurado.</p>
     *
     * @param termo texto digitado na busca
     * @return itens cujo título ou autor/diretor contém o termo (sem diferenciar maiúsculas)
     * @throws SQLException em caso de erro no banco
     */
    public List<ItemMidia> buscarSeguro(String termo) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM item_midia "
                + "WHERE LOWER(titulo) LIKE ? OR LOWER(autor_diretor) LIKE ? "
                + "ORDER BY titulo";
        String padrao = "%" + termo.toLowerCase() + "%";
        try (Connection conn = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, padrao);
            stmt.setString(2, padrao);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapear(rs);
            }
        }
    }

    /** Converte as linhas do ResultSet em objetos ItemMidia. */
    private List<ItemMidia> mapear(ResultSet rs) throws SQLException {
        List<ItemMidia> itens = new ArrayList<>();
        while (rs.next()) {
            ItemMidia item = new ItemMidia();
            item.setId(rs.getInt("id"));
            item.setTitulo(rs.getString("titulo"));
            item.setAutorDiretor(rs.getString("autor_diretor"));
            int ano = rs.getInt("ano_lancamento");
            item.setAnoLancamento(rs.wasNull() ? null : ano);
            item.setGenero(rs.getString("genero"));
            item.setSinopse(rs.getString("sinopse"));
            item.setTipoMidia(rs.getString("tipo_midia"));
            itens.add(item);
        }
        return itens;
    }
}
