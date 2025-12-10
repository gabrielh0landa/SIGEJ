package sigej

import grails.converters.JSON

class EstoqueController {

    EstoqueService estoqueService

    static responseFormats = ['json']

    def listar() {
        try {
            respond estoqueService.listarProdutosEmEstoque()
        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao listar estoque: ${e.message}")
        }
    }

    def buscar() {
        try {
            Long variacaoId = params.long('variacaoId')
            Long localId = params.long('localId')

            if (!variacaoId || !localId) {
                render(status: 400, text: 'Parâmetros variacaoId e localId são obrigatórios')
                return
            }

            def resultado = estoqueService.buscarEstoqueDeVariacao(variacaoId, localId)

            if (resultado) {
                respond resultado
            } else {
                render(status: 404, text: 'Produto não encontrado neste local de estoque.')
            }

        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao buscar item: ${e.message}")
        }
    }

    def movimentar() {
        try {
            def dados = request.JSON

            if (!dados.variacaoId || !dados.localId || !dados.quantidade || !dados.tipoMovimentoId) {
                render(status: 400, text: 'Campos obrigatórios: variacaoId, localId, quantidade, tipoMovimentoId')
                return
            }

            estoqueService.movimentarEstoque(
                    dados.variacaoId as Long,
                    dados.localId as Long,
                    dados.quantidade as BigDecimal,
                    dados.tipoMovimentoId as Long,
                    dados.funcionarioId ? dados.funcionarioId as Long : null,
                    dados.osId ? dados.osId as Long : null,
                    dados.observacao
            )

            render(status: 200, text: 'Movimentação realizada com sucesso.')

        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao movimentar estoque: ${e.message}")
        }
    }
}