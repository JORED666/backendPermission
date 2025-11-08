package students.infrastructure

import core.ConnMySQL
import students.domain.IStudentRepository
import students.infrastructure.adapters.MySQLStudentRepository
import students.application.*
import students.infrastructure.controllers.*

data class DependenciesStudents(
    val createStudentController: CreateStudentController,
    val getAllStudentController: GetAllStudentController,
    val getStudentByIdController: GetStudentByIdController,
    val getStudentsByTutorIdController: GetStudentsByTutorIdController,
    val searchStudentController: SearchStudentController,
    val updateStudentController: UpdateStudentController,
    val deleteStudentController: DeleteStudentController,
    val studentRepository: IStudentRepository
)

fun initStudents(conn: ConnMySQL): DependenciesStudents {
    val studentRepository: IStudentRepository = MySQLStudentRepository(conn)
    
    val createStudentUseCase = CreateStudentUseCase(studentRepository)
    val getAllStudentsWithDetailsUseCase = GetAllStudentsWithDetailsUseCase(studentRepository)
    val getStudentByIdWithDetailsUseCase = GetStudentByIdWithDetailsUseCase(studentRepository)
    val getStudentsByTutorIdUseCase = GetStudentsByTutorIdUseCase(studentRepository)
    val searchStudentUseCase = SearchStudentUseCase(studentRepository)
    val updateStudentUseCase = UpdateStudentUseCase(studentRepository)
    val deleteStudentUseCase = DeleteStudentUseCase(studentRepository)

    return DependenciesStudents(
        createStudentController = CreateStudentController(createStudentUseCase),
        getAllStudentController = GetAllStudentController(getAllStudentsWithDetailsUseCase),
        getStudentByIdController = GetStudentByIdController(getStudentByIdWithDetailsUseCase),
        getStudentsByTutorIdController = GetStudentsByTutorIdController(getStudentsByTutorIdUseCase),
        searchStudentController = SearchStudentController(searchStudentUseCase),
        updateStudentController = UpdateStudentController(updateStudentUseCase),
        deleteStudentController = DeleteStudentController(deleteStudentUseCase),
        studentRepository = studentRepository
    )
}