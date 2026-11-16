package br.edu.pit.catalogo.web;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/** Funções auxiliares para ler parâmetros da requisição HTTP com segurança. */
final class Requisicoes {

    private Requisicoes() {
    }

    /**
     * Lê um parâmetro de texto, devolvendo string vazia quando ausente.
     *
     * @param req  requisição
     * @param nome nome do parâmetro
     * @return valor aparado ({@code trim}) ou {@code ""}
     */
    static String texto(HttpServletRequest req, String nome) {
        String valor = req.getParameter(nome);
        return valor == null ? "" : valor.trim();
    }

    /**
     * Lê o parâmetro {@code id}. Valores ausentes, não numéricos ou menores que 1
     * são rejeitados, para que um id malformado (ex.: {@code ?id=1 OR 1=1}) nunca
     * chegue ao banco.
     *
     * @param req requisição
     * @return o id, ou {@link Optional#empty()} se for inválido
     */
    static Optional<Integer> lerId(HttpServletRequest req) {
        try {
            int id = Integer.parseInt(texto(req, "id"));
            return id > 0 ? Optional.of(id) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
