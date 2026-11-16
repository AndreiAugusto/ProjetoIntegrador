package br.edu.pit.catalogo.web;

import br.edu.pit.catalogo.dao.FabricaDeConexoes;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.SQLException;

/** Cria as tabelas e os dados de exemplo quando a aplicação sobe. */
@WebListener
public class InicializadorBanco implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        try {
            FabricaDeConexoes.inicializarBanco();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível inicializar o banco de dados", e);
        }
    }
}
