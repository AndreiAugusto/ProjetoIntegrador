package br.edu.pit.catalogo.servico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes unitários das regras de validação usadas no cadastro e na edição. */
class ValidadorItemMidiaTest {

    @Test
    @DisplayName("Dados corretos passam e o item é montado (com trim)")
    void dadosValidos() {
        var r = ValidadorItemMidia.validar("  Dom Casmurro ", "Machado de Assis", "1899",
                "Romance", "Sinopse", "Livro");

        assertTrue(r.valido());
        assertEquals("Dom Casmurro", r.item().getTitulo());
        assertEquals(1899, r.item().getAnoLancamento());
        assertEquals("Livro", r.item().getTipoMidia());
    }

    @Test
    @DisplayName("Ano vazio é aceito e fica nulo")
    void anoOpcional() {
        var r = ValidadorItemMidia.validar("Título", "", "", "", "", "Filme");
        assertTrue(r.valido());
        assertNull(r.item().getAnoLancamento());
    }

    @Test
    @DisplayName("Título vazio é recusado")
    void tituloObrigatorio() {
        var r = ValidadorItemMidia.validar("   ", "", "", "", "", "Livro");
        assertFalse(r.valido());
        assertTrue(r.erros().contains("O título é obrigatório."));
    }

    @Test
    @DisplayName("Ano com texto é recusado sem lançar exceção")
    void anoNaoNumerico() {
        var r = ValidadorItemMidia.validar("Título", "", "abc", "", "", "Livro");
        assertFalse(r.valido());
        assertTrue(r.erros().contains("O ano deve ser um número."));
        assertNull(r.item().getAnoLancamento());
    }

    @Test
    @DisplayName("Ano fora da faixa 1000–2100 é recusado")
    void anoForaDaFaixa() {
        assertFalse(ValidadorItemMidia.validar("T", "", "999", "", "", "Livro").valido());
        assertFalse(ValidadorItemMidia.validar("T", "", "2101", "", "", "Livro").valido());
        assertTrue(ValidadorItemMidia.validar("T", "", "2100", "", "", "Livro").valido());
    }

    @Test
    @DisplayName("Tipo fora da lista é recusado")
    void tipoInvalido() {
        var r = ValidadorItemMidia.validar("Título", "", "", "", "", "Podcast");
        assertFalse(r.valido());
        assertTrue(r.erros().contains("Escolha o tipo: Livro, Filme ou Série."));
    }

    @Test
    @DisplayName("Campos maiores que as colunas do banco são recusados")
    void tamanhosMaximos() {
        String t256 = "a".repeat(256);
        String s2001 = "a".repeat(2001);
        var r = ValidadorItemMidia.validar(t256, t256, "", "a".repeat(101), s2001, "Livro");
        assertEquals(4, r.erros().size());
    }

    @Test
    @DisplayName("Parâmetros nulos são tratados como vazios e todos os erros aparecem juntos")
    void nulosEErrosAcumulados() {
        var r = ValidadorItemMidia.validar(null, null, "x", null, null, null);
        assertEquals(3, r.erros().size()); // título, tipo e ano
    }
}
