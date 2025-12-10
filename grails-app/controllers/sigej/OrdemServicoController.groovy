package sigej

import grails.converters.JSON
import java.text.SimpleDateFormat

class OrdemServicoController {

    OrdemServicoService ordemServicoService

    static responseFormats = ['json']

    def listar() {
        try {
            respond ordemServicoService.listarOrdens()
        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao listar ordens: ${e.message}")
        }
    }

    def buscar() {
        try {
            Long id = params.long('id')
            if (!id) {
                render(status: 400, text: 'ID é obrigatório')
                return
            }
            def ordem = ordemServicoService.buscarOrdem(id)
            if (ordem) {
                respond ordem
            } else {
                render(status: 404, text: 'Ordem de serviço não encontrada')
            }
        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao buscar ordem: ${e.message}")
        }
    }

    def itens() {
        try {
            Long osId = params.long('osId')
            if (!osId) {
                render(status: 400, text: 'osId é obrigatório')
                return
            }
            respond ordemServicoService.listarItens(osId)
        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao listar itens: ${e.message}")
        }
    }

    def criar() {
        try {
            def dados = request.JSON

            if (!dados.numero_sequencial || !dados.solicitante_id || !dados.tipo_os_id) {
                render(status: 400, text: 'Campos obrigatórios faltando (numero_sequencial, solicitante_id, tipo_os_id)')
                return
            }

            if (dados.data_prevista) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
                dados.data_prevista = sdf.parse(dados.data_prevista)
            }

            ordemServicoService.criarOrdem(dados)
            render(status: 201, text: 'Ordem criada com sucesso')

        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao criar ordem: ${e.message}")
        }
    }

    def atualizarStatus() {
        try {
            def dados = request.JSON

            if (!dados.osId || !dados.statusNovoId || !dados.funcionarioId) {
                render(status: 400, text: 'Campos obrigatórios: osId, statusNovoId, funcionarioId')
                return
            }

            ordemServicoService.atualizarStatus(
                    dados.osId as Long,
                    dados.statusNovoId as Long,
                    dados.funcionarioId as Long,
                    dados.descricao
            )

            render(status: 200, text: 'Status atualizado com sucesso')

        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao atualizar status: ${e.message}")
        }
    }
}