package permition.infrastructure

import core.ConnMySQL
import permition.domain.PermitRepository
import permition.infrastructure.adapters.MySQLPermitRepository
import permition.application.*
import permition.infrastructure.controllers.*
import notify.application.NotificationService

data class DependenciesPermits(
    val createPermitController: CreatePermitController,
    val getAllPermitsController: GetAllPermitsController,
    val getPermitByIdController: GetPermitByIdController,
    val updatePermitController: UpdatePermitController,
    val deletePermitController: DeletePermitController
)

fun initPermits(
    conn: ConnMySQL,
    notificationService: NotificationService
): DependenciesPermits {
    val permitRepository: PermitRepository = MySQLPermitRepository(conn)
    
    val createPermitUseCase = CreatePermitUseCase(permitRepository)
    val getAllPermitsWithDetailsUseCase = GetAllPermitsWithDetailsUseCase(permitRepository)
    val getPermitByIdWithDetailsUseCase = GetPermitByIdWithDetailsUseCase(permitRepository)
    val getPermitByIdUseCase = GetPermitByIdUseCase(permitRepository)
    val updatePermitUseCase = UpdatePermitUseCase(permitRepository)
    val deletePermitUseCase = DeletePermitUseCase(permitRepository)

    return DependenciesPermits(
        createPermitController = CreatePermitController(
            createPermitUseCase, 
            getPermitByIdWithDetailsUseCase,
            notificationService
        ),
        getAllPermitsController = GetAllPermitsController(getAllPermitsWithDetailsUseCase),
        getPermitByIdController = GetPermitByIdController(getPermitByIdWithDetailsUseCase),
        updatePermitController = UpdatePermitController(
            updatePermitUseCase, 
            getPermitByIdUseCase,
            notificationService
        ),
        deletePermitController = DeletePermitController(deletePermitUseCase)
    )
}