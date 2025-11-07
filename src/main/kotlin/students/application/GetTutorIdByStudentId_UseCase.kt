package students.application

import students.domain.IStudentRepository

class GetTutorIdByStudentIdUseCase(private val studentRepository: IStudentRepository) {
    suspend fun execute(studentId: Int): Int? {
        println("Buscando tutor para estudiante ID: $studentId")

        val tutorId = studentRepository.getTutorIdByStudentId(studentId)

        if (tutorId != null) {
            println("Tutor encontrado: $tutorId")
        } else {
            println("No se encontró tutor para el estudiante $studentId")
        }
        
        return tutorId
    }
}
