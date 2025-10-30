package teachers.infrastructure

import core.ConnMySQL
import teachers.domain.ITeacherRepository
import teachers.infrastructure.adapters.MySQLTeacherRepository
import teachers.application.*
import teachers.infrastructure.controllers.*

data class DependenciesTeachers(
    val createTeacherController: CreateTeacherController,
    val getAllTeacherController: GetAllTeacherController,
    val getTeacherByIdController: GetTeacherByIdController,
    val updateTeacherController: UpdateTeacherController,
    val deleteTeacherController: DeleteTeacherController
)

fun initTeachers(conn: ConnMySQL): DependenciesTeachers {
    val teacherRepository: ITeacherRepository = MySQLTeacherRepository(conn)
    
    val createTeacherUseCase = CreateTeacherUseCase(teacherRepository)
    val getAllTeachersWithDetailsUseCase = GetAllTeachersWithDetailsUseCase(teacherRepository)
    val getTeacherByIdWithDetailsUseCase = GetTeacherByIdWithDetailsUseCase(teacherRepository)
    val updateTeacherUseCase = UpdateTeacherUseCase(teacherRepository)
    val deleteTeacherUseCase = DeleteTeacherUseCase(teacherRepository)

    return DependenciesTeachers(
        createTeacherController = CreateTeacherController(createTeacherUseCase),
        getAllTeacherController = GetAllTeacherController(getAllTeachersWithDetailsUseCase),
        getTeacherByIdController = GetTeacherByIdController(getTeacherByIdWithDetailsUseCase),
        updateTeacherController = UpdateTeacherController(updateTeacherUseCase),
        deleteTeacherController = DeleteTeacherController(deleteTeacherUseCase)
    )
}