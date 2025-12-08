package manutencao

import grails.converters.JSON

class PessoaController {

    PessoaService pessoaService

    static responseFormats = ['json']
    static allowedMethods = [index: 'GET', save: 'POST', show: 'GET', update: 'PUT', delete: 'DELETE']

    def index() {
        render pessoaService.listarPessoas() as JSON
    }

    def show(Long id) {
        def pessoa = pessoaService.buscarPorId(id)
        if (pessoa) {
            render pessoa as JSON
        } else {
            render status: 404
        }
    }

    def save() {
        try {
            println "=== PESSOA SAVE ==="
            def dados = request.JSON
            println "Dados: ${dados}"

            pessoaService.inserirPessoa(dados)

            response.status = 201
            render([sucesso: true, mensagem: "Pessoa criada"] as JSON)

        } catch (Exception e) {
            println "ERRO: ${e.message}"
            response.status = 400
            render([sucesso: false, erro: e.message] as JSON)
        }
    }

    def update(Long id) {
        try {
            def dados = request.JSON
            pessoaService.atualizarPessoa(id, dados)
            render([sucesso: true, mensagem: "Pessoa atualizada"] as JSON)
        } catch (Exception e) {
            render([sucesso: false, erro: e.message] as JSON, status: 400)
        }
    }

    def delete(Long id) {
        try {
            pessoaService.excluirPessoa(id)
            render status: 204
        } catch (Exception e) {
            render([sucesso: false, erro: e.message] as JSON, status: 400)
        }
    }
}