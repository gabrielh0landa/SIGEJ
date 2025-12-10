package sigej

import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult

@Transactional
class ConsultaService {

    def groovySql

    List<GroovyRowResult> ordensEmAberto() {
        String sql = '''
            SELECT os.id,
                   os.numero_sequencial,
                   os.prioridade,
                   ac.descricao AS area,
                   tos.descricao AS tipo_servico,
                   p.nome AS solicitante,
                   os.data_abertura,
                   sts.descricao AS status
              FROM ordem_servico os
              JOIN area_campus ac ON os.area_campus_id = ac.id
              JOIN tipo_ordem_servico tos ON os.tipo_os_id = tos.id
              JOIN status_ordem_servico sts ON os.status_id = sts.id
              JOIN pessoa p ON os.solicitante_id = p.id
             WHERE sts.descricao IN ('Aberta', 'Em Andamento', 'Aguardando Peças')
             ORDER BY os.prioridade ASC, os.data_abertura ASC
        '''
        groovySql.rows(sql)
    }

    List<GroovyRowResult> materiaisAbaixoPontoReposicao() {
        String sql = '''
            SELECT p.descricao,
                   pv.codigo_interno,
                   le.descricao AS local,
                   e.quantidade,
                   e.ponto_reposicao,
                   (e.ponto_reposicao - e.quantidade) AS deficit
              FROM estoque e
              JOIN produto_variacao pv ON e.produto_variacao_id = pv.id
              JOIN produto p ON pv.produto_id = p.id
              JOIN local_estoque le ON e.local_estoque_id = le.id
             WHERE e.quantidade < e.ponto_reposicao
             ORDER BY deficit DESC
        '''
        groovySql.rows(sql)
    }

    List<GroovyRowResult> timelineOrdemServico(Long ordemServicoId) {
        String sql = '''
            SELECT a.data_hora,
                   pes.nome AS funcionario,
                   sts_ant.descricao AS status_anterior,
                   sts_novo.descricao AS status_novo,
                   a.descricao,
                   a.inicio_atendimento,
                   a.fim_atendimento
              FROM andamento_ordem_servico a
              JOIN funcionario f ON a.funcionario_id = f.id
              JOIN pessoa pes ON f.pessoa_id = pes.id
              JOIN status_ordem_servico sts_novo ON a.status_novo_id = sts_novo.id
              LEFT JOIN status_ordem_servico sts_ant ON a.status_anterior_id = sts_ant.id
             WHERE a.os_id = :id
             ORDER BY a.data_hora
        '''
        groovySql.rows(sql, [id: ordemServicoId])
    }

    List<GroovyRowResult> consumoPorEquipe(Date dataInicio, Date dataFim) {
        String sql = '''
            SELECT eq.nome AS equipe,
                   p.descricao AS produto,
                   SUM(me.quantidade) AS total_consumido,
                   um.sigla AS unidade
              FROM movimento_estoque me
              JOIN produto_variacao pv ON me.produto_variacao_id = pv.id
              JOIN produto p ON pv.produto_id = p.id
              JOIN unidade_medida um ON p.unidade_medida_id = um.id
              JOIN ordem_servico os ON me.ordem_servico_id = os.id
              JOIN equipe_manutencao eq ON os.equipe_id = eq.id
              JOIN tipo_movimento_estoque tme ON me.tipo_movimento_id = tme.id
             WHERE me.data_hora BETWEEN :inicio AND :fim
               AND tme.sinal = '-'
             GROUP BY eq.nome, p.descricao, um.sigla
             ORDER BY eq.nome, total_consumido DESC
        '''

        def params = [
                inicio: new java.sql.Timestamp(dataInicio.time),
                fim   : new java.sql.Timestamp(dataFim.time)
        ]

        groovySql.rows(sql, params)
    }

    List<GroovyRowResult> ordensConcluidasPorTipo(Integer ano) {
        String sql = '''
            SELECT tos.descricao AS tipo_servico,
                   COUNT(*) AS total_concluidas,
                   ROUND(
                       AVG(
                           EXTRACT(EPOCH FROM (aos.fim_atendimento - aos.inicio_atendimento)) / 3600
                       ),
                       2
                   ) AS tempo_medio_horas
              FROM ordem_servico os
              JOIN tipo_ordem_servico tos ON os.tipo_os_id = tos.id
              JOIN status_ordem_servico sts ON os.status_id = sts.id
              LEFT JOIN andamento_ordem_servico aos
                     ON os.id = aos.os_id
                    AND aos.status_novo_id = (
                        SELECT id FROM status_ordem_servico WHERE descricao = 'Concluída'
                    )
             WHERE sts.descricao = 'Concluída'
               AND EXTRACT(YEAR FROM os.data_abertura) = :ano
             GROUP BY tos.descricao
             ORDER BY total_concluidas DESC
        '''
        groovySql.rows(sql, [ano: ano])
    }

    List<GroovyRowResult> relatorioOrdensServico(Map filtros) {
        String where = ''
        def params = [:]

        if (filtros.status) {
            where += ' AND sts.descricao = :status'
            params.status = filtros.status
        }

        if (filtros.tipo) {
            where += ' AND tos.descricao = :tipo'
            params.tipo = filtros.tipo
        }

        if (filtros.dataInicio && filtros.dataFim) {
            where += ' AND os.data_abertura BETWEEN :inicio AND :fim'
            params.inicio = new java.sql.Date(filtros.dataInicio.time)
            params.fim    = new java.sql.Date(filtros.dataFim.time)
        }

        String sql = """
            SELECT os.id,
                   os.numero_sequencial,
                   os.prioridade,
                   ac.descricao AS area,
                   tos.descricao AS tipo,
                   sts.descricao AS status,
                   p.nome AS solicitante,
                   eq.nome AS equipe,
                   os.data_abertura,
                   os.data_prevista,
                   (SELECT COUNT(*) FROM item_ordem_servico WHERE os_id = os.id) AS total_itens
              FROM ordem_servico os
              JOIN area_campus ac ON os.area_campus_id = ac.id
              JOIN tipo_ordem_servico tos ON os.tipo_os_id = tos.id
              JOIN status_ordem_servico sts ON os.status_id = sts.id
              JOIN pessoa p ON os.solicitante_id = p.id
              LEFT JOIN equipe_manutencao eq ON os.equipe_id = eq.id
             WHERE 1=1 ${where}
             ORDER BY os.prioridade, os.data_abertura
        """

        groovySql.rows(sql, params)
    }
}
