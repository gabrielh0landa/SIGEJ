package sigej

import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult

@Transactional
class EstoqueService {

    def groovySql

    List<GroovyRowResult> listarProdutosEmEstoque() {
        String sql = '''
            SELECT pv.id AS produto_variacao_id,
                   pr.descricao AS produto,
                   c.nome AS cor,
                   t.descricao AS tamanho,
                   le.descricao AS local,
                   e.quantidade,
                   e.ponto_reposicao
              FROM estoque e
              JOIN produto_variacao pv ON pv.id = e.produto_variacao_id
              JOIN produto pr ON pr.id = pv.produto_id
              LEFT JOIN cor c ON c.id = pv.cor_id
              LEFT JOIN tamanho t ON t.id = pv.tamanho_id
              JOIN local_estoque le ON le.id = e.local_estoque_id
             ORDER BY pr.descricao
        '''
        groovySql.rows(sql)
    }

    GroovyRowResult buscarEstoqueDeVariacao(Long variacaoId, Long localId) {
        String sql = '''
            SELECT *
              FROM estoque
             WHERE produto_variacao_id = :vid
               AND local_estoque_id = :lid
        '''
        groovySql.firstRow(sql, [vid: variacaoId, lid: localId])
    }

    void movimentarEstoque(Long variacaoId, Long localId, BigDecimal quantidade,
                           Long tipoMovimentoId, Long funcionarioId, Long osId, String observacao) {

        String sqlSinal = '''
            SELECT sinal
              FROM tipo_movimento_estoque
             WHERE id = :id
        '''
        def rowTipo = groovySql.firstRow(sqlSinal, [id: tipoMovimentoId])

        String sinal = rowTipo?.sinal ?: '+'

        BigDecimal fator = sinal == '-' ? -1 : 1
        BigDecimal delta = quantidade * fator

        String sqlUpdate = '''
            UPDATE estoque
               SET quantidade = quantidade + :delta
             WHERE produto_variacao_id = :vid
               AND local_estoque_id = :lid
        '''
        int updated = groovySql.executeUpdate(sqlUpdate, [delta: delta, vid: variacaoId, lid: localId])

        if (updated == 0) {
            String sqlInsert = '''
                INSERT INTO estoque (produto_variacao_id, local_estoque_id, quantidade, ponto_reposicao)
                VALUES (:vid, :lid, :quantidade, 0)
            '''
            groovySql.executeInsert(sqlInsert, [
                    vid       : variacaoId,
                    lid       : localId,
                    quantidade: quantidade
            ])
        }

        String sqlMov = '''
            INSERT INTO movimento_estoque
                (produto_variacao_id, local_estoque_id, tipo_movimento_id,
                 quantidade, data_hora, funcionario_id, ordem_servico_id, observacao)
            VALUES
                (:vid, :lid, :tipoId, :qtd, NOW(), :funcId, :osId, :obs)
        '''

        groovySql.executeInsert(sqlMov, [
                vid    : variacaoId,
                lid    : localId,
                tipoId : tipoMovimentoId,
                qtd    : quantidade,
                funcId : funcionarioId,
                osId   : osId,
                obs    : observacao
        ])
    }
}