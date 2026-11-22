# Sistema de Gestão de Funcionários

Projeto de Aplicação de POO em Java (interface gráfica Swing) com persistência em SQLite via JDBC.

## Conceitos aplicados
- **Herança:** `Gerente` e `Caixa` estendem a superclasse abstrata `Funcionario`.
- **Polimorfismo:** `calcularBonificacao()` usa `getPercentualBonus()`, implementado de forma diferente em cada subclasse (Gerente 20%, Caixa 10%). A tabela trata todos como `Funcionario`.
- **Banco de dados:** SQLite com JDBC nativo (`Connection`, `PreparedStatement`, `ResultSet`), padrão DAO.

## Estrutura
```
src/model  -> Funcionario (abstrata), Gerente, Caixa
src/db     -> Conexao (JDBC + criação da tabela), FuncionarioDAO (CRUD)
src/app    -> TelaPrincipal (interface Swing)
lib        -> sqlite-jdbc.jar
```

## Como executar
Requer Java 17+ (testado no 21). No Windows, execute `compilar-e-executar.bat`.

Manualmente:
```
javac -encoding UTF-8 -d out src/model/*.java src/db/*.java src/app/*.java
java -cp "out;lib/sqlite-jdbc.jar" app.TelaPrincipal      (Linux/Mac: use ":" no lugar de ";")
```
O arquivo `funcionarios.db` é criado automaticamente na pasta de execução.

## Funcionalidades
Cadastrar Gerente/Caixa, listar (com bônus e salário final), atualizar salário, excluir e total da folha.

## Integrantes
- (preencher)
