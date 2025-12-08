import groovy.sql.Sql
// Place your Spring DSL code here
beans = {
    groovySql(Sql, ref('dataSource'))
}
