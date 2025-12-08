package manutencao

import groovy.sql.GroovyRowResult

class EstoqueService {

    def groovySql

    List<GroovyRowResult> listarProdutosEmEstoque() {
        String sql = '''
            select pv.id as produto_variacao_id,
                   pr.descricao as produto,
                   c.nome as cor,
                   t.descricao as tamanho,
                   le.descricao as local,
                   e.quantidade,
                   e.ponto_reposicao
              from estoque e
              join produto_variacao pv on pv.id = e.produto_variacao_id
              join produto pr on pr.id = pv.produto_id
              left join cor c on c.id = pv.cor_id
              left join tamanho t on t.id = pv.tamanho_id
              join local_estoque le on le.id = e.local_estoque_id
             order by pr.descricao
        '''
        groovySql.rows(sql)
    }

    GroovyRowResult buscarEstoqueDeVariacao(Long variacaoId, Long localId) {
        String sql = '''
            select *
              from estoque
             where produto_variacao_id = :vid
               and local_estoque_id = :lid
        '''
        groovySql.firstRow(sql, [vid: variacaoId, lid: localId])
    }

    void movimentarEstoque(Long variacaoId, Long localId, BigDecimal quantidade,
                           Long tipoMovimentoId, Long funcionarioId, Long osId, String observacao) {

        String sqlSinal = '''
            select sinal
              from tipo_movimento_estoque
             where id = :id
        '''
        def rowTipo = groovySql.firstRow(sqlSinal, [id: tipoMovimentoId])
        String sinal = rowTipo?.sinal ?: '+'

        BigDecimal fator = sinal == '-' ? -1 : 1
        BigDecimal delta = quantidade * fator

        String sqlUpdate = '''
            update estoque
               set quantidade = quantidade + :delta
             where produto_variacao_id = :vid
               and local_estoque_id = :lid
        '''
        int updated = groovySql.executeUpdate(sqlUpdate, [delta: delta, vid: variacaoId, lid: localId])

        if (updated == 0) {
            String sqlInsert = '''
                insert into estoque (produto_variacao_id, local_estoque_id, quantidade, ponto_reposicao)
                values (:vid, :lid, :quantidade, 0)
            '''
            groovySql.executeInsert(sqlInsert, [
                    vid       : variacaoId,
                    lid       : localId,
                    quantidade: quantidade
            ])
        }

        String sqlMov = '''
            insert into movimento_estoque
                (produto_variacao_id, local_estoque_id, tipo_movimento_id,
                 quantidade, data_hora, funcionario_id, ordem_servico_id, observacao)
            values
                (:vid, :lid, :tipoId, :qtd, now(), :funcId, :osId, :obs)
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
