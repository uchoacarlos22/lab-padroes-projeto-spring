# Explorando Padrões de Projetos na Prática com Java

Repositório com as implementações dos padrões de projeto explorados no Lab "Explorando Padrões de Projetos na Prática com Java". Especificamente, este projeto explorou alguns padrões usando o Spring Framework, são eles:
- Singleton
- Strategy/Repository
- Facade

## Evoluções

A API de clientes também demonstra respostas HTTP apropriadas: `POST /clientes` retorna `201 Created` com o endereço do recurso, `DELETE /clientes/{id}` retorna `204 No Content`, e consultas/alterações de clientes inexistentes retornam `404`. CEPs aceitam os formatos `12345678` e `12345-678`; o endereço é consultado no ViaCEP apenas quando ainda não está persistido. CEP inválido ou inexistente retorna `400` com uma mensagem no campo `erro`.

### Exemplos

```http
POST /clientes
Content-Type: application/json

{
  "nome": "Maria Silva",
  "endereco": {
    "cep": "01001-000"
  }
}
```

Os demais recursos estão disponíveis em `GET /clientes`, `GET /clientes/{id}`, `PUT /clientes/{id}` e `DELETE /clientes/{id}`. Para criar e consultar clientes localmente, execute a aplicação com `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). O banco H2 em memória é reiniciado ao encerrar a aplicação.

Execute os testes com `./mvnw test` (Windows: `mvnw.cmd test`).
