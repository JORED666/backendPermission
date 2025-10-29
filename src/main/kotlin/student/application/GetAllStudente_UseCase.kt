

class GetAllStudente_UseCase(private val repository: StudnetRepository){

    suspend operator fun invoke(): StudentsListResponse {

        val students = repository.getAllStudents()

        return StudentsListResponse(

            success = true,
            message = if (students.isEmpty())"Nog hay estudiantes registrados"else "Estudiantes obtenidos exitosamente"
            data = students
        )
    }


}