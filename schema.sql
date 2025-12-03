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

CREATE TABLE "tipo_area_campus" (
  "id" serial PRIMARY KEY,
  "descricao" varchar(50)
);

CREATE TABLE "area_campus" (
  "id" serial PRIMARY KEY,
  "tipo_area_id" int,
  "descricao" varchar(100) NOT NULL,
  "bloco" varchar(10)
);

CREATE TABLE "equipe_manutencao" (
  "id" serial PRIMARY KEY,
  "nome" varchar(80) NOT NULL,
  "turno" varchar(20)
);

CREATE TABLE "categoria_material" (
  "id" serial PRIMARY KEY,
  "nome" varchar(60) NOT NULL
);

CREATE TABLE "unidade_medida" (
  "id" serial PRIMARY KEY,
  "sigla" varchar(10) NOT NULL,
  "descricao" varchar(50)
);

CREATE TABLE "fornecedor" (
  "id" serial PRIMARY KEY,
  "nome" varchar(100),
  "cnpj" varchar(18)
);

CREATE TABLE "marca" (
  "id" serial PRIMARY KEY,
  "nome" varchar(80)
);

CREATE TABLE "cor" (
  "id" serial PRIMARY KEY,
  "nome" varchar(30)
);

CREATE TABLE "tamanho" (
  "id" serial PRIMARY KEY,
  "descricao" varchar(30)
);

CREATE TABLE "produto" (
  "id" serial PRIMARY KEY,
  "descricao" text NOT NULL,
  "categoria_id" int,
  "unidade_medida_id" int,
  "marca_id" int
);

CREATE TABLE "produto_variacao" (
  "id" serial PRIMARY KEY,
  "produto_id" int,
  "cor_id" int,
  "tamanho_id" int,
  "codigo_barras" varchar(50) UNIQUE,
  "codigo_interno" varchar(30)
);

CREATE TABLE "local_estoque" (
  "id" serial PRIMARY KEY,
  "descricao" varchar(100),
  "responsavel_id" int
);

CREATE TABLE "estoque" (
  "produto_variacao_id" int,
  "local_estoque_id" int,
  "quantidade" decimal(10,3) DEFAULT 0,
  "ponto_reposicao" decimal(10,3) DEFAULT 0,
  PRIMARY KEY ("produto_variacao_id", "local_estoque_id")
);

CREATE TABLE "tipo_movimento_estoque" (
  "id" serial PRIMARY KEY,
  "descricao" varchar(50),
  "sinal" char(1)
);

CREATE TABLE "tipo_ordem_servico" (
  "id" serial PRIMARY KEY,
  "descricao" varchar(80)
);

CREATE TABLE "status_ordem_servico" (
  "id" serial PRIMARY KEY,
  "descricao" varchar(50)
);

CREATE TABLE "funcionario" (
  "id" serial PRIMARY KEY,
  "pessoa_id" int,
  "tipo_funcionario_id" int,
  "setor_id" int,
  "data_admissao" date,
  "data_demissao" date
);

CREATE TABLE "equipe_membro" (
  "id" serial PRIMARY KEY,
  "equipe_id" int,
  "funcionario_id" int,
  "data_inicio" date NOT NULL,
  "data_fim" date,
  "funcao" varchar(30)
);

CREATE TABLE "movimento_estoque" (
  "id" serial PRIMARY KEY,
  "produto_variacao_id" int,
  "local_estoque_id" int,
  "tipo_movimento_id" int,
  "quantidade" decimal(10,3) NOT NULL,
  "data_hora" timestamp,
  "funcionario_id" int,
  "ordem_servico_id" int,
  "observacao" text
);

CREATE TABLE "ordem_servico" (
  "id" serial PRIMARY KEY,
  "numero_sequencial" varchar(20) UNIQUE,
  "solicitante_id" int,
  "area_campus_id" int,
  "tipo_os_id" int,
  "equipe_id" int,
  "lider_id" int,
  "status_id" int,
  "prioridade" int,
  "data_abertura" timestamp,
  "data_prevista" date,
  "descricao_problema" text
);

CREATE TABLE "item_ordem_servico" (
  "id" serial PRIMARY KEY,
  "os_id" int,
  "produto_variacao_id" int,
  "quantidade_prevista" decimal(10,3),
  "quantidade_usada" decimal(10,3)
);

CREATE TABLE "andamento_ordem_servico" (
  "id" serial PRIMARY KEY,
  "os_id" int,
  "data_hora" timestamp,
  "status_anterior_id" int,
  "status_novo_id" int,
  "funcionario_id" int,
  "descricao" text,
  "inicio_atendimento" timestamp,
  "fim_atendimento" timestamp
);

ALTER TABLE "funcionario" ADD FOREIGN KEY ("pessoa_id") REFERENCES "pessoa" ("id");

ALTER TABLE "funcionario" ADD FOREIGN KEY ("tipo_funcionario_id") REFERENCES "tipo_funcionario" ("id");

ALTER TABLE "funcionario" ADD FOREIGN KEY ("setor_id") REFERENCES "setor" ("id");

ALTER TABLE "area_campus" ADD FOREIGN KEY ("tipo_area_id") REFERENCES "tipo_area_campus" ("id");

ALTER TABLE "equipe_membro" ADD FOREIGN KEY ("equipe_id") REFERENCES "equipe_manutencao" ("id");

ALTER TABLE "equipe_membro" ADD FOREIGN KEY ("funcionario_id") REFERENCES "funcionario" ("id");

ALTER TABLE "produto" ADD FOREIGN KEY ("categoria_id") REFERENCES "categoria_material" ("id");

ALTER TABLE "produto" ADD FOREIGN KEY ("unidade_medida_id") REFERENCES "unidade_medida" ("id");

ALTER TABLE "produto" ADD FOREIGN KEY ("marca_id") REFERENCES "marca" ("id");

ALTER TABLE "produto_variacao" ADD FOREIGN KEY ("produto_id") REFERENCES "produto" ("id");

ALTER TABLE "produto_variacao" ADD FOREIGN KEY ("cor_id") REFERENCES "cor" ("id");

ALTER TABLE "produto_variacao" ADD FOREIGN KEY ("tamanho_id") REFERENCES "tamanho" ("id");

ALTER TABLE "local_estoque" ADD FOREIGN KEY ("responsavel_id") REFERENCES "funcionario" ("id");

ALTER TABLE "estoque" ADD FOREIGN KEY ("produto_variacao_id") REFERENCES "produto_variacao" ("id");

ALTER TABLE "estoque" ADD FOREIGN KEY ("local_estoque_id") REFERENCES "local_estoque" ("id");

ALTER TABLE "movimento_estoque" ADD FOREIGN KEY ("produto_variacao_id") REFERENCES "produto_variacao" ("id");

ALTER TABLE "movimento_estoque" ADD FOREIGN KEY ("local_estoque_id") REFERENCES "local_estoque" ("id");

ALTER TABLE "movimento_estoque" ADD FOREIGN KEY ("tipo_movimento_id") REFERENCES "tipo_movimento_estoque" ("id");

ALTER TABLE "movimento_estoque" ADD FOREIGN KEY ("funcionario_id") REFERENCES "funcionario" ("id");

ALTER TABLE "ordem_servico" ADD FOREIGN KEY ("solicitante_id") REFERENCES "pessoa" ("id");

ALTER TABLE "ordem_servico" ADD FOREIGN KEY ("area_campus_id") REFERENCES "area_campus" ("id");

ALTER TABLE "ordem_servico" ADD FOREIGN KEY ("tipo_os_id") REFERENCES "tipo_ordem_servico" ("id");

ALTER TABLE "ordem_servico" ADD FOREIGN KEY ("equipe_id") REFERENCES "equipe_manutencao" ("id");

ALTER TABLE "ordem_servico" ADD FOREIGN KEY ("lider_id") REFERENCES "funcionario" ("id");

ALTER TABLE "ordem_servico" ADD FOREIGN KEY ("status_id") REFERENCES "status_ordem_servico" ("id");

ALTER TABLE "item_ordem_servico" ADD FOREIGN KEY ("os_id") REFERENCES "ordem_servico" ("id");

ALTER TABLE "item_ordem_servico" ADD FOREIGN KEY ("produto_variacao_id") REFERENCES "produto_variacao" ("id");

ALTER TABLE "andamento_ordem_servico" ADD FOREIGN KEY ("os_id") REFERENCES "ordem_servico" ("id");

ALTER TABLE "andamento_ordem_servico" ADD FOREIGN KEY ("status_anterior_id") REFERENCES "status_ordem_servico" ("id");

ALTER TABLE "andamento_ordem_servico" ADD FOREIGN KEY ("status_novo_id") REFERENCES "status_ordem_servico" ("id");

ALTER TABLE "andamento_ordem_servico" ADD FOREIGN KEY ("funcionario_id") REFERENCES "funcionario" ("id");
