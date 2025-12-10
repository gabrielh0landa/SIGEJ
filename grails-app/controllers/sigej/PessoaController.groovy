package sigej

import grails.converters.JSON

class PessoaController {

    PessoaService pessoaService

    static responseFormats = ['json']

    def listar() {
        try {
            respond pessoaService.listarPessoas()
        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao listar pessoas: ${e.message}")
        }
    }

    def buscar() {
        try {
            Long id = params.long('id')
            if (!id) {
                render(status: 400, text: 'ID é obrigatório')
                return
            }

            def pessoa = pessoaService.buscarPessoaPorId(id)
            if (pessoa) {
                respond pessoa
            } else {
                render(status: 404, text: 'Pessoa não encontrada')
            }
        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao buscar pessoa: ${e.message}")
        }
    }

    def salvar() {
        try {
            def dados = request.JSON

            if (!dados.nome || !dados.cpf) {
                render(status: 400, text: 'Nome e CPF são obrigatórios')
                return
            }

            pessoaService.inserirPessoa(dados)
            render(status: 201, text: 'Pessoa cadastrada com sucesso')

        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao cadastrar pessoa: ${e.message}")
        }
    }

    def atualizar() {
        try {
            def dados = request.JSON

            Long id = dados.id ? dados.id as Long : params.long('id')

            if (!id) {
                render(status: 400, text: 'ID da pessoa é obrigatório para atualização')
                return
            }

            pessoaService.atualizarPessoa(id, dados)
            render(status: 200, text: 'Pessoa atualizada com sucesso')

        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao atualizar pessoa: ${e.message}")
        }
    }

    def excluir() {
        try {
            Long id = params.long('id')
            if (!id) {
                render(status: 400, text: 'ID é obrigatório')
                return
            }

            pessoaService.excluirPessoa(id)
            render(status: 200, text: 'Pessoa excluída com sucesso')

        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao excluir pessoa: ${e.message}")
        }
    }
}