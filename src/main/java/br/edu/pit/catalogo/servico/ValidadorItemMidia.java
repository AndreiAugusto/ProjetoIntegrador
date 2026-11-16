package br.edu.pit.catalogo.servico;

import br.edu.pit.catalogo.model.ItemMidia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Regras de validação de um {@link ItemMidia}, usadas tanto no cadastro quanto na edição.
 *
 * <p>Concentrar a validação aqui evita repetir as mesmas regras em várias Servlets e
 * permite testá-las com JUnit sem precisar de um servidor web. Os limites de tamanho
 * acompanham o tamanho das colunas da tabela {@code item_midia}.</p>
 */
public final class ValidadorItemMidia {

    /** Tipos de mídia aceitos pelo catálogo. */
    public static final Set<String> TIPOS = Set.of("Livro", "Filme", "Série");

    public static final int MAX_TITULO = 255;
    public static final int MAX_AUTOR = 255;
    public static final int MAX_GENERO = 100;
    public static final int MAX_SINOPSE = 2000;
    public static final int ANO_MINIMO = 1000;
    public static final int ANO_MAXIMO = 2100;

    private ValidadorItemMidia() {
    }

    /**
     * Resultado da validação: o item montado com os dados recebidos e a lista de erros.
     *
     * @param item  item preenchido com os valores informados (o ano fica nulo se for inválido)
     * @param erros mensagens de erro para exibir ao usuário (vazia quando tudo está certo)
     */
    public record Resultado(ItemMidia item, List<String> erros) {

        /** @return {@code true} quando não há nenhum erro de validação */
        public boolean valido() {
            return erros.isEmpty();
        }
    }

    /**
     * Valida os campos do formulário e monta o item.
     *
     * <p>Valores {@code null} são tratados como texto vazio e todos os campos são
     * aparados ({@code trim}). Todas as regras são verificadas, para que o usuário
     * veja todos os problemas de uma só vez.</p>
     *
     * @param titulo       título (obrigatório, até 255 caracteres)
     * @param autorDiretor autor ou diretor (opcional, até 255 caracteres)
     * @param anoTexto     ano como texto (opcional; se informado, número entre 1000 e 2100)
     * @param genero       gênero (opcional, até 100 caracteres)
     * @param sinopse      sinopse (opcional, até 2000 caracteres)
     * @param tipoMidia    tipo (obrigatório: Livro, Filme ou Série)
     * @return item montado e lista de erros
     */
    public static Resultado validar(String titulo, String autorDiretor, String anoTexto,
                                    String genero, String sinopse, String tipoMidia) {
        titulo = limpar(titulo);
        autorDiretor = limpar(autorDiretor);
        anoTexto = limpar(anoTexto);
        genero = limpar(genero);
        sinopse = limpar(sinopse);
        tipoMidia = limpar(tipoMidia);

        List<String> erros = new ArrayList<>();
        if (titulo.isEmpty()) erros.add("O título é obrigatório.");
        if (titulo.length() > MAX_TITULO) erros.add("O título deve ter no máximo " + MAX_TITULO + " caracteres.");
        if (autorDiretor.length() > MAX_AUTOR) erros.add("O autor/diretor deve ter no máximo " + MAX_AUTOR + " caracteres.");
        if (genero.length() > MAX_GENERO) erros.add("O gênero deve ter no máximo " + MAX_GENERO + " caracteres.");
        if (sinopse.length() > MAX_SINOPSE) erros.add("A sinopse deve ter no máximo " + MAX_SINOPSE + " caracteres.");
        if (!TIPOS.contains(tipoMidia)) erros.add("Escolha o tipo: Livro, Filme ou Série.");

        Integer ano = null;
        if (!anoTexto.isEmpty()) {
            try {
                ano = Integer.parseInt(anoTexto);
                if (ano < ANO_MINIMO || ano > ANO_MAXIMO) {
                    erros.add("Informe um ano entre " + ANO_MINIMO + " e " + ANO_MAXIMO + ".");
                }
            } catch (NumberFormatException e) {
                // Situação-problema 2: texto no campo ano não pode derrubar a aplicação
                erros.add("O ano deve ser um número.");
            }
        }

        ItemMidia item = new ItemMidia(titulo, autorDiretor, ano, genero, sinopse, tipoMidia);
        return new Resultado(item, Collections.unmodifiableList(erros));
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
