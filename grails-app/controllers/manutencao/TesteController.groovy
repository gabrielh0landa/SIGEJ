package manutencao

class TesteController {

    def index() {
        render "Teste OK"
    }

    def salvar() {
        println "Headers: ${request.headers}"
        println "Content-Type: ${request.contentType}"
        println "Body: ${request.reader.text}"
        render([status: 'ok'] as grails.converters.JSON)
    }
}