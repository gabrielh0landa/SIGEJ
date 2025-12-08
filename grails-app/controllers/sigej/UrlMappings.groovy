package sigej

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
            }
        }

        get "/consultas/ordens-aberto"(controller: "consulta", action: "ordensAberto")
        get "/consultas/materiais-reposicao"(controller: "consulta", action: "materiaisReposicao")
        get "/consultas/timeline/$id"(controller: "consulta", action: "timelineOS")
        get "/consultas/consumo-equipe"(controller: "consulta", action: "consumoEquipe")
        get "/consultas/concluidas-tipo"(controller: "consulta", action: "concluidasPorTipo")
        get "/consultas/relatorio-os"(controller: "consulta", action: "relatorioOS")

        "/"(controller: 'application', action: 'index')
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
