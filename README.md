
# SIGEJ – Sistema de Informação de Gestão de Jardinagem e Manutenção

[![Java](https://img.shields.io/badge/Java-8%2F11-blue)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
[![Grails](https://img.shields.io/badge/Grails-4.0-green)](https://grails.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)](https://github.com/SEU_USUARIO/SIGEJ)

---

## Descrição

O **SIGEJ** é um sistema backend para gestão de jardinagem e manutenção, implementado com **Grails/Groovy**, utilizando **Java** e **PostgreSQL**. Ele fornece APIs REST que permitem gerenciar ordens de serviço, estoque, equipes e materiais, sem o uso de ORMs. Todas as operações SQL são feitas manualmente.

---

## Tecnologias Utilizadas

- **Linguagens:** Groovy (Grails Framework) + Java  
- **Banco de Dados:** PostgreSQL  
- **Build:** Gradle  
- **Testes de API:** Postman  
- **IDE Recomendada:** IntelliJ IDEA  

---

## Modelagem de Dados

O sistema possui **25 tabelas**, todas normalizadas e relacionadas.  
Exemplo de algumas tabelas:
* pessoa, funcionario, setor, area_campus, equipe_manutencao

* produto, produto_variacao, estoque, movimento_estoque

* ordem_servico, item_ordem_servico, andamento_ordem_servico

```sql
CREATE TABLE "pessoa" (
  "id" serial PRIMARY KEY,
  "nome" varchar(100) NOT NULL,
  "cpf" varchar(11) UNIQUE,
  "matricula_siape" varchar(20),
  "email" varchar(100),
  "telefone" varchar(20),
  "ativo" boolean DEFAULT true
);

CREATE TABLE "tipo_funcionario" (
  "id" serial PRIMARY KEY,
  "descricao" varchar(50) NOT NULL
);

CREATE TABLE "setor" (
  "id" serial PRIMARY KEY,
  "nome" varchar(80) NOT NULL,
  "sigla" varchar(10)
);
````

> O projeto inclui **script completo** com todas as tabelas, chaves primárias e estrangeiras.

---

## Consultas SQL Principais

* **Ordens de serviço abertas por prioridade e área**
* **Materiais abaixo do ponto de reposição**
* **Timeline de uma OS**
* **Consumo por equipe em determinado período**
* **OS concluídas por tipo no ano**

> Essas consultas podem ser executadas diretamente no PostgreSQL ou via aplicação.

---

## Configuração do Banco de Dados

* Crie o banco PostgreSQL:

```sql
CREATE DATABASE sigej;
```

* Usuário e senha padrão (pode alterar no `application.yml`):

```
username: postgres
password: postgres
```

> Grails conecta automaticamente ao banco via `application.yml`.
* Configurações de conexão (definidas no `application.yml`):

```
Host: localhost
Porta: 5432
URL: jdbc:postgresql://localhost:5432/sigej
Usuário: postgres
Senha: postgres
```

---

## Rodando a Aplicação

1. Clone o repositório:

```bash
git clone <URL_DO_REPOSITORIO>
cd sigej
```

2. Execute a aplicação:

```bash
./grailsw run-app
```

> O servidor será iniciado em `http://localhost:8080`.

---

## Testando a API

A aplicação disponibiliza **APIs REST**.
É recomendado usar **Postman** para testar as requisições.

Exemplo de GET para listar ordens de serviço:

```
GET http://localhost:8080/ordem_servico
```
---

## Estrutura do Projeto

```
SIGEJ/
├─ grails-app/
├─ src/
│  ├─ main/
│  └─ test/
├─ build.gradle
├─ application.yml
└─ README.md
```

---

## Observações

* Todas as queries são **manualmente escritas**, sem ORM.
* Java 8 ou 11 é obrigatório; Java 21 não funciona.
* Postman facilita testes de API e verificação de retorno JSON.

---

## Contato

Projeto desenvolvido como parte da disciplina de **Banco de Dados**.
Para dúvidas, entre em contato com o desenvolvedor responsável.

