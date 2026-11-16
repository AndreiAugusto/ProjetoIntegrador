# Catálogo Simples – Situação-problema 1 (SQL Injection)

Projeto de intervenção da disciplina **Projeto Integrador Transdisciplinar (PIT) em Ciência da Computação**.

Um catálogo web de livros e filmes em **Java + Servlets + JSP/JSTL + JDBC** que mostra, lado a lado:

- a **busca vulnerável**, que concatena o texto digitado no SQL (o problema da Situação-problema 1);
- a **busca corrigida**, com `PreparedStatement`.

## Funcionalidades

| Rota | O que faz |
|------|-----------|
| `/itens` | lista todos os itens (título leva aos detalhes) |
| `/cadastro` | cadastra um novo item (com validação) |
| `/item?id=N` | mostra os detalhes de um item, com botões Editar e Excluir |
| `/editar?id=N` | edita um item (mesmas validações do cadastro) |
| `POST /excluir` | exclui um item (só por POST, com confirmação) |
| `/busca` | busca por título ou autor/diretor (segura ou vulnerável, para a demonstração) |

Todo o SQL do CRUD usa `PreparedStatement`; a única consulta concatenada é a `buscarInseguro`, mantida de propósito para a demonstração.

## Requisitos

- Java 17 ou superior (`java -version`)
- Maven 3.8+ (`mvn -version`)

No Ubuntu/Debian: `sudo apt install openjdk-17-jdk maven`

## Como rodar

```bash
mvn test        # roda os 21 testes (ataque/correção, CRUD e validação)
mvn jetty:run   # sobe a aplicação em http://localhost:8080
```

O banco H2 é criado sozinho na pasta `dados/`, já com 5 itens de exemplo. Não é preciso instalar banco de dados.

## Demonstração do ataque

1. Abra http://localhost:8080/busca
2. Escolha **Busca vulnerável** e pesquise `' OR 1=1 --` → aparecem **todos** os itens.
3. Pesquise `' UNION SELECT id, login, senha_hash, 0, 'x', 'x', 'x' FROM usuario --` → o login e o hash da tabela `usuario` aparecem na lista de livros (vazamento de dados).
4. Troque para **Busca segura** e repita → **0 resultados**: o texto é tratado só como dado.

## Estrutura (MVC)

```
src/main/java/br/edu/pit/catalogo/
├── model/ItemMidia.java               entidade (encapsulamento)
├── dao/FabricaDeConexoes.java         conexão JDBC e criação das tabelas
├── dao/ItemMidiaDAO.java              todo o SQL: CRUD + buscarInseguro x buscarSeguro
├── servico/ValidadorItemMidia.java    regras de validação (cadastro e edição)
└── web/                               Servlets (controladores)
src/main/webapp/WEB-INF/jsp/           telas JSP (c:out contra XSS)
src/test/java/.../SqlInjectionTest     testes do ataque e da correção
src/test/java/.../ItemMidiaDAOCrudTest testes do CRUD no banco H2 em memória
src/test/java/.../ValidadorItemMidiaTest testes das validações
```

## Usar PostgreSQL em vez do H2 (opcional)

```bash
mvn jetty:run -Dcatalogo.db.url=jdbc:postgresql://localhost:5432/catalogo \
  -Dcatalogo.db.usuario=postgres -Dcatalogo.db.senha=SUA_SENHA \
  -Dcatalogo.db.driver=org.postgresql.Driver
```

> A busca vulnerável existe apenas para fins didáticos. Nunca concatene entradas do usuário em SQL.
