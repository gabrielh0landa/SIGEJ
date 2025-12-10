package sigej
import java.text.SimpleDateFormat

class ConsultaController {

    ConsultaService consultaService

    static responseFormats = ['json']

    def ordensAberto() {
        try {
            respond consultaService.ordensEmAberto()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def materiaisAbaixoPontoReposicao() {
        try {
            respond consultaService.materiaisAbaixoPontoReposicao()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def timelineOrdemServico(Long id) {
        try {
            respond consultaService.timelineOrdemServico(id)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def consumoPorEquipe() {
        try {
            def sdf = new SimpleDateFormat("yyyy-MM-dd")

            Date inicio = params.inicio ? sdf.parse(params.inicio) : null
            Date fim    = params.fim    ? sdf.parse(params.fim)    : null

            if (!inicio || !fim) {
                render(status: 400, text: 'Parâmetros inicio e fim são obrigatórios (formato yyyy-MM-dd)')
                return
            }

            respond consultaService.consumoPorEquipe(inicio, fim)
        } catch (Exception e) {
            e.printStackTrace()
            render(status: 500, text: "Erro ao processar: ${e.message}")
        }
    }

    def ordensConcluidasPorTipo(Integer ano) {
        try {
            Integer anoFiltro = ano ?: params.int('ano')
            if (!anoFiltro) {
                render(status: 400, text: 'Parâmetro ano é obrigatório')
                return
            }

            respond consultaService.ordensConcluidasPorTipo(anoFiltro)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def relatorioOrdensServico() {
        try {
            def filtros = [:]

            if (params.status) {
                filtros.status = params.status
            }
            if (params.tipo) {
                filtros.tipo = params.tipo
            }
            if (params.dataInicio && params.dataFim) {
                filtros.dataInicio = Date.parse('yyyy-MM-dd', params.dataInicio)
                filtros.dataFim    = Date.parse('yyyy-MM-dd', params.dataFim)
            }

            respond consultaService.relatorioOrdensServico(filtros)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}
