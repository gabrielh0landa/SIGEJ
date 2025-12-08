package manutencao

import groovy.sql.GroovyRowResult

class PessoaService {

    def groovySql

    List<GroovyRowResult> listarPessoas() {
        String sql = '''
            select id, nome, cpf, email, telefone, ativo
            from pessoa
            order by nome
        '''
        groovySql.rows(sql)
    }

    GroovyRowResult buscarPessoaPorId(Long id) {
        String sql = '''
            select id, nome, cpf, email, telefone, ativo
            from pessoa
            where id = :id
        '''
        groovySql.firstRow(sql, [id: id])
    }

    void inserirPessoa(Map dados) {
        String sql = '''
            insert into pessoa (nome, cpf, matricula_siape, email, telefone, ativo)
            values (:nome, :cpf, :matricula, :email, :telefone, :ativo)
        '''
        groovySql.executeInsert(sql, [
                nome      : dados.nome,
                cpf       : dados.cpf,
                matricula : dados.matricula_siape,
                email     : dados.email,
                telefone  : dados.telefone,
                ativo     : dados.ativo != null ? dados.ativo : true
        ])
    }

    void atualizarPessoa(Long id, Map dados) {
        String sql = '''
            update pessoa
               set nome = :nome,
                   cpf = :cpf,
                   matricula_siape = :matricula,
                   email = :email,
                   telefone = :telefone,
                   ativo = :ativo
             where id = :id
        '''
        groovySql.executeUpdate(sql, [
                id        : id,
                nome      : dados.nome,
                cpf       : dados.cpf,
                matricula : dados.matricula_siape,
                email     : dados.email,
                telefone  : dados.telefone,
                ativo     : dados.ativo
        ])
    }

    void excluirPessoa(Long id) {
        String sql = 'delete from pessoa where id = :id'
        groovySql.executeUpdate(sql, [id: id])
    }
}
