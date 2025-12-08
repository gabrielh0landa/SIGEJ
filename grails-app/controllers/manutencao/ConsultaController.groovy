package manutencao

import grails.converters.JSON

class ConsultaController {

    ConsultaService consultaService

    static responseFormats = ['json']
    static allowedMethods = [
            ordensAberto: 'GET',
            materiaisReposicao: 'GET',
            timelineOS: 'GET',
            consumoEquipe: 'GET',
            concluidasPorTipo: 'GET',
            relatorioOS: 'GET'
    ]

    def ordensAberto() {
        try {
            def resultado = consultaService.ordensEmAberto()
            render([
                    total: resultado.size(),
                    ordens: resultado
            ] as JSON)
        } catch (Exception e) {
            render([erro: e.message] as JSON, status: 500)
        }
    }

    def materiaisReposicao() {
        try {
            def resultado = consultaService.materiaisAbaixoPontoReposicao()
            render([
                    total: resultado.size(),
                    materiais: resultado
            ] as JSON)
        } catch (Exception e) {
            render([erro: e.message] as JSON, status: 500)
        }
    }

    def timelineOS(Long id) {
        try {
            if (!id) {
                render([erro: "ID da OS não informado"] as JSON, status: 400)
                return
            }

            def resultado = consultaService.timelineOrdemServico(id)
            render([
                    ordem_servico_id: id,
                    total_andamentos: resultado.size(),
                    timeline: resultado
            ] as JSON)
        } catch (Exception e) {
            render([erro: e.message] as JSON, status: 500)
        }
    }

    def consumoEquipe(Date inicio, Date fim) {
        try {
            if (!inicio) {
                inicio = new Date().clearTime()
                inicio.set(date: 1) // Primeiro dia do mês
            }

            if (!fim) {
                fim = new Date()
            }

            def resultado = consultaService.consumoPorEquipe(inicio, fim)

            render([
                    periodo: [inicio: inicio, fim: fim],
                    total_equipes: resultado.groupBy { it.equipe }.size(),
                    consumo: resultado
            ] as JSON)
        } catch (Exception e) {
            render([erro: e.message] as JSON, status: 500)
        }
    }

    def concluidasPorTipo(Integer ano) {
        try {
            if (!ano) {
                ano = Calendar.getInstance().get(Calendar.YEAR)
            }

            def resultado = consultaService.ordensConcluidasPorTipo(ano)

            render([
                    ano: ano,
                    total_concluidas: resultado.sum { it.total_concluidas ?: 0 },
                    resumo: resultado
            ] as JSON)
        } catch (Exception e) {
            render([erro: e.message] as JSON, status: 500)
        }
    }

    def relatorioOS(String status, String tipo, Date inicio, Date fim) {
        try {
            // Corrigir encoding do parâmetro (se necessário)
            if (status) {
                status = URLDecoder.decode(status, "UTF-8")
            }
            if (tipo) {
                tipo = URLDecoder.decode(tipo, "UTF-8")
            }

            def filtros = [
                    status: status,
                    tipo: tipo,
                    dataInicio: inicio,
                    dataFim: fim
            ]

            def resultado = consultaService.relatorioOrdensServico(filtros)

            render([
                    filtros: filtros,
                    total: resultado.size(),
                    ordens: resultado
            ] as JSON)
        } catch (Exception e) {
            render([erro: e.message] as JSON, status: 500)
        }
    }
}