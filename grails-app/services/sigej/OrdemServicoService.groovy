package sigej

import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult
import java.sql.Timestamp

@Transactional
class OrdemServicoService {

    def groovySql

    List<GroovyRowResult> listarOrdens() {
        String sql = '''
            SELECT os.id,
                   os.numero_sequencial,
                   os.data_abertura,
                   os.prioridade,
                   s.descricao AS status,
                   p.nome      AS solicitante
              FROM ordem_servico os
              JOIN status_ordem_servico s ON s.id = os.status_id
              JOIN pessoa p ON p.id = os.solicitante_id
             ORDER BY os.data_abertura DESC
        '''
        groovySql.rows(sql)
    }

    GroovyRowResult buscarOrdem(Long id) {
        String sql = '''
            SELECT os.*,
                   s.descricao AS status,
                   p.nome      AS solicitante
              FROM ordem_servico os
              JOIN status_ordem_servico s ON s.id = os.status_id
              JOIN pessoa p ON p.id = os.solicitante_id
             WHERE os.id = :id
        '''
        groovySql.firstRow(sql, [id: id])
    }

    List<GroovyRowResult> listarItens(Long osId) {
        String sql = '''
            SELECT ios.id,
                   ios.quantidade_prevista,
                   ios.quantidade_usada,
                   pv.codigo_interno,
                   pr.descricao AS produto
              FROM item_ordem_servico ios
              JOIN produto_variacao pv ON pv.id = ios.produto_variacao_id
              JOIN produto pr ON pr.id = pv.produto_id
             WHERE ios.os_id = :osId
        '''
        groovySql.rows(sql, [osId: osId])
    }

    void criarOrdem(Map dados) {
        String sql = '''
            INSERT INTO ordem_servico
                (numero_sequencial, solicitante_id, area_campus_id,
                 tipo_os_id, equipe_id, lider_id, status_id,
                 prioridade, data_abertura, data_prevista, descricao_problema)
            VALUES
                (:numero, :solicitanteId, :areaId,
                 :tipoOsId, :equipeId, :liderId, :statusId,
                 :prioridade, NOW(), :dataPrevista, :descricao)
        '''

        def dataPrevistaSql = dados.data_prevista ? new Timestamp(dados.data_prevista.time) : null

        groovySql.executeInsert(sql, [
                numero       : dados.numero_sequencial,
                solicitanteId: dados.solicitante_id,
                areaId       : dados.area_campus_id,
                tipoOsId     : dados.tipo_os_id,
                equipeId     : dados.equipe_id,
                liderId      : dados.lider_id,
                statusId     : dados.status_id,
                prioridade   : dados.prioridade,
                dataPrevista : dataPrevistaSql,
                descricao    : dados.descricao_problema
        ])
    }

    void atualizarStatus(Long osId, Long statusNovoId, Long funcionarioId, String descricao) {
        String sqlBusca = "SELECT status_id FROM ordem_servico WHERE id = :id"
        def row = groovySql.firstRow(sqlBusca, [id: osId])
        Long statusAnteriorId = row?.status_id

        String sqlUpdate = '''
            UPDATE ordem_servico
               SET status_id = :statusId
             WHERE id = :id
        '''
        groovySql.executeUpdate(sqlUpdate, [statusId: statusNovoId, id: osId])

        String sqlHistorico = '''
            INSERT INTO andamento_ordem_servico
                (os_id, data_hora, status_anterior_id, status_novo_id, funcionario_id, descricao)
            VALUES
                (:osId, NOW(), :statusAnteriorId, :statusNovoId, :funcionarioId, :descricao)
        '''
        groovySql.executeInsert(sqlHistorico, [
                osId            : osId,
                statusAnteriorId: statusAnteriorId,
                statusNovoId    : statusNovoId,
                funcionarioId   : funcionarioId,
                descricao       : descricao
        ])
    }
}