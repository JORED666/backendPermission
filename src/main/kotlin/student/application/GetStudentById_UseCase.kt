

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class GetStudentById_UseCase (private val repository: StudentRepository){

    suspend operator fun invoke (studentId: Int): StudentResponse{
        val student = repository.getStudentById(studentId)

        return if (student != null){
            StudentResponse{
                success = true,
                message = "Estudiante encontrado",
                data = student
            }
        } else {
            StudentResponse (
                success = false,
                message = "Estudiante no encontrado"
            )
        }
    }
}