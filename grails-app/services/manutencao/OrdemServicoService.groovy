package manutencao

import groovy.sql.GroovyRowResult

class OrdemServicoService {

    def groovySql

    List<GroovyRowResult> listarOrdens() {
        String sql = '''
            select os.id,
                   os.numero_sequencial,
                   os.data_abertura,
                   os.prioridade,
                   s.descricao as status,
                   p.nome      as solicitante
              from ordem_servico os
              join status_ordem_servico s on s.id = os.status_id
              join pessoa p on p.id = os.solicitante_id
             order by os.data_abertura desc
        '''
        groovySql.rows(sql)
    }

    GroovyRowResult buscarOrdem(Long id) {
        String sql = '''
            select os.*,
                   s.descricao as status,
                   p.nome      as solicitante
              from ordem_servico os
              join status_ordem_servico s on s.id = os.status_id
              join pessoa p on p.id = os.solicitante_id
             where os.id = :id
        '''
        groovySql.firstRow(sql, [id: id])
    }

    List<GroovyRowResult> listarItens(Long osId) {
        String sql = '''
            select ios.id,
                   ios.quantidade_prevista,
                   ios.quantidade_usada,
                   pv.codigo_interno,
                   pr.descricao as produto
              from item_ordem_servico ios
              join produto_variacao pv on pv.id = ios.produto_variacao_id
              join produto pr on pr.id = pv.produto_id
             where ios.os_id = :osId
        '''
        groovySql.rows(sql, [osId: osId])
    }

    void criarOrdem(Map dados) {
        String sql = '''
            insert into ordem_servico
                (numero_sequencial, solicitante_id, area_campus_id,
                 tipo_os_id, equipe_id, lider_id, status_id,
                 prioridade, data_abertura, data_prevista, descricao_problema)
            values
                (:numero, :solicitanteId, :areaId,
                 :tipoOsId, :equipeId, :liderId, :statusId,
                 :prioridade, now(), :dataPrevista, :descricao)
        '''
        groovySql.executeInsert(sql, [
                numero       : dados.numero_sequencial,
                solicitanteId: dados.solicitante_id,
                areaId       : dados.area_campus_id,
                tipoOsId     : dados.tipo_os_id,
                equipeId     : dados.equipe_id,
                liderId      : dados.lider_id,
                statusId     : dados.status_id,
                prioridade   : dados.prioridade,
                dataPrevista : dados.data_prevista,
                descricao    : dados.descricao_problema
        ])
    }

    void atualizarStatus(Long osId, Long statusNovoId, Long funcionarioId, String descricao) {
        String sqlUpdate = '''
            update ordem_servico
               set status_id = :statusId
             where id = :id
        '''
        groovySql.executeUpdate(sqlUpdate, [statusId: statusNovoId, id: osId])

        String sqlHistorico = '''
            insert into andamento_ordem_servico
                (os_id, data_hora, status_anterior_id, status_novo_id, funcionario_id, descricao)
            values
                (:osId, now(), :statusAnteriorId, :statusNovoId, :funcionarioId, :descricao)
        '''
        groovySql.executeInsert(sqlHistorico, [
                osId            : osId,
                statusAnteriorId: null,       // se quiser, busque antes
                statusNovoId    : statusNovoId,
                funcionarioId   : funcionarioId,
                descricao       : descricao
        ])
    }
}
