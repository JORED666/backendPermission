package permition.infrastructure.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import permition.infrastructure.controllers.*

fun Application.configurePermitRoutes(
    createPermitController: CreatePermitController,
    getAllPermitsController: GetAllPermitsController,
    getPermitByIdController: GetPermitByIdController,
    updatePermitController: UpdatePermitController,
    deletePermitController: DeletePermitController
) {
    routing {
        route("/api") {
            route("/permits") {
                // Crear permiso con archivo PDF
                post {
                    createPermitController.execute(call)
                }
                
                // Obtener todos los permisos
                get {
                    getAllPermitsController.execute(call)
                }
                
                // Obtener permiso por ID
                get("/{id}") {
                    getPermitByIdController.execute(call)
                }
                
                // Actualizar permiso
                put("/{id}") {
                    updatePermitController.execute(call)
                }
                
                // Eliminar permiso
                delete("/{id}") {
                    deletePermitController.execute(call)
                }
            }
        }
    }
}