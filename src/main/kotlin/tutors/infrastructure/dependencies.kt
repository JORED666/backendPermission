package tutors.infrastructure

import core.ConnMySQL
import tutors.domain.ITutorRepository  
import tutors.infrastructure.adapters.MySQLTutorRepository
import tutors.application.*
import tutors.infrastructure.controller.*

data class DependenciesTutors(
    val createTutorController: CreateTutorController,
    val getAllTutorsController: GetAllTutorsController,
    val getTutorByIdController: GetTutorByIdController,
    val updateTutorController: UpdateTutorController,
    val deleteTutorController: DeleteTutorController,
    val tutorRepository: ITutorRepository
)

fun initTutors(conn: ConnMySQL): DependenciesTutors {
    val tutorRepository = MySQLTutorRepository(conn)
    
    val createTutorUseCase = CreateTutorUseCase(tutorRepository)
    val getAllTutorsUseCase = GetAllTutorsWithDetailsUseCase(tutorRepository)
    val getTutorByIdUseCase = GetTutorByIdWithDetailsUseCase(tutorRepository)
    val updateTutorUseCase = UpdateTutorUseCase(tutorRepository)
    val deleteTutorUseCase = DeleteTutorUseCase(tutorRepository)

    return DependenciesTutors(
        createTutorController = CreateTutorController(createTutorUseCase),
        getAllTutorsController = GetAllTutorsController(getAllTutorsUseCase),
        getTutorByIdController = GetTutorByIdController(getTutorByIdUseCase),
        updateTutorController = UpdateTutorController(updateTutorUseCase),
        deleteTutorController = DeleteTutorController(deleteTutorUseCase),
        tutorRepository = tutorRepository  
    )
}