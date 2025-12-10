package sigej

import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult

@Transactional
class PessoaService {

    def groovySql

    List<GroovyRowResult> listarPessoas() {
        String sql = '''
            SELECT id, nome, cpf, email, telefone, ativo
              FROM pessoa
             ORDER BY nome
        '''
        groovySql.rows(sql)
    }

    GroovyRowResult buscarPessoaPorId(Long id) {
        String sql = '''
            SELECT id, nome, cpf, email, telefone, ativo
              FROM pessoa
             WHERE id = :id
        '''
        groovySql.firstRow(sql, [id: id])
    }

    void inserirPessoa(Map dados) {
        String sql = '''
            INSERT INTO pessoa (nome, cpf, matricula_siape, email, telefone, ativo)
            VALUES (:nome, :cpf, :matricula, :email, :telefone, :ativo)
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
            UPDATE pessoa
               SET nome = :nome,
                   cpf = :cpf,
                   matricula_siape = :matricula,
                   email = :email,
                   telefone = :telefone,
                   ativo = :ativo
             WHERE id = :id
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
        String sql = 'DELETE FROM pessoa WHERE id = :id'
        groovySql.executeUpdate(sql, [id: id])
    }
}