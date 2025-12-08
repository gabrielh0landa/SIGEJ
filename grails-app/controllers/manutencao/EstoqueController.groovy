package manutencao

import grails.converters.JSON

class EstoqueController {

    EstoqueService estoqueService

    static responseFormats = ['json']
    static allowedMethods = [index: 'GET', movimentar: 'POST']

    def index() {
        render estoqueService.listarProdutosEmEstoque() as JSON
    }

    def movimentar() {
        def body = request.JSON
        estoqueService.movimentarEstoque(
                body.produto_variacao_id as Long,
                body.local_estoque_id as Long,
                body.quantidade as BigDecimal,
                body.tipo_movimento_id as Long,
                body.funcionario_id as Long,
                body.ordem_servico_id as Long,
                body.observacao as String
        )
        render status: 200
    }
}
