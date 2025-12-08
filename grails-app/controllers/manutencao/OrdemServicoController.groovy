package manutencao

import grails.converters.JSON

class OrdemServicoController {

    OrdemServicoService ordemServicoService

    static responseFormats = ['json']
    static allowedMethods = [index: 'GET', show: 'GET', save: 'POST', itens: 'GET', mudarStatus: 'POST']

    def index() {
        render ordemServicoService.listarOrdens() as JSON
    }

    def show(Long id) {
        def os = ordemServicoService.buscarOrdem(id)
        if (!os) {
            render status: 404
            return
        }
        render os as JSON
    }

    def itens(Long id) {
        render ordemServicoService.listarItens(id) as JSON
    }

    def save() {
        def body = request.JSON
        ordemServicoService.criarOrdem(body)
        render status: 201
    }

    def mudarStatus(Long id) {
        def body = request.JSON
        ordemServicoService.atualizarStatus(
                id,
                body.status_novo_id as Long,
                body.funcionario_id as Long,
                body.descricao as String
        )
        render status: 200
    }
}
