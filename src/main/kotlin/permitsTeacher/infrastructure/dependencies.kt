package permitsTeacher.infrastructure

import core.ConnMySQL
import permitsTeacher.domain.IPermitTeacherRepository
import permitsTeacher.infrastructure.adapters.MySQLPermitTeacherRepository
import permitsTeacher.application.*
import permitsTeacher.infrastructure.controllers.*

data class DependenciesPermitTeacher(
    val createPermitTeacherController: CreatePermitTeacherController,
    val getAllPermitTeacherController: GetAllPermitTeacherController,
    val getPermitTeacherByIdController: GetPermitTeacherByIdController,
    val deletePermitTeacherController: DeletePermitTeacherController
)

fun initPermitTeacher(conn: ConnMySQL): DependenciesPermitTeacher {
    val permitTeacherRepository: IPermitTeacherRepository = MySQLPermitTeacherRepository(conn)
    
    val createPermitTeacherUseCase = CreatePermitTeacherUseCase(permitTeacherRepository)
    val getAllPermitTeacherUseCase = GetAllPermitTeacherUseCase(permitTeacherRepository)
    val getPermitTeacherUseCase = GetPermitTeacherUseCase(permitTeacherRepository)
    val deletePermitTeacherUseCase = DeletePermitTeacherUseCase(permitTeacherRepository)

    return DependenciesPermitTeacher(
        createPermitTeacherController = CreatePermitTeacherController(createPermitTeacherUseCase),
        getAllPermitTeacherController = GetAllPermitTeacherController(getAllPermitTeacherUseCase),
        getPermitTeacherByIdController = GetPermitTeacherByIdController(getPermitTeacherUseCase),
        deletePermitTeacherController = DeletePermitTeacherController(deletePermitTeacherUseCase)
    )
}