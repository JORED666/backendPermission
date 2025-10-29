package permition.infrastructure

import core.ConnMySQL
import permition.infrastructure.adapters.MySQLPermitRepository
import permition.application.*
import permition.infrastructure.controllers.*

data class DependenciesPermits(
    val createPermitController: CreatePermitController,
    val getAllPermitsController: GetAllPermitsController,
    val getPermitByIdController: GetPermitByIdController,
    val updatePermitController: UpdatePermitController,
    val deletePermitController: DeletePermitController
)

fun initPermits(conn: ConnMySQL): DependenciesPermits {
    val permitRepository = MySQLPermitRepository(conn)
    
    val createPermitUseCase = CreatePermitUseCase(permitRepository)
    val getAllPermitsUseCase = GetAllPermitsUseCase(permitRepository)
    val getPermitByIdUseCase = GetPermitByIdUseCase(permitRepository)
    val updatePermitUseCase = UpdatePermitUseCase(permitRepository)
    val deletePermitUseCase = DeletePermitUseCase(permitRepository)

    return DependenciesPermits(
        createPermitController = CreatePermitController(createPermitUseCase),
        getAllPermitsController = GetAllPermitsController(getAllPermitsUseCase),
        getPermitByIdController = GetPermitByIdController(getPermitByIdUseCase),
        updatePermitController = UpdatePermitController(updatePermitUseCase, getPermitByIdUseCase),
        deletePermitController = DeletePermitController(deletePermitUseCase)
    )
}