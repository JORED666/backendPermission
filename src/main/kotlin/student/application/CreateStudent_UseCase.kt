package student.application

class CreateStudent_UseCase(private val repository: StudentRepository){
    suspend operator fun invoke (request: CreateStudentRequest): StudentResponse {
         //validar que la matricula no este vacia
        if (request.enrollmentNumber.isBlank()){
            return StudentResponse(
                success = false,
                message = "El numero de matricula no puede estar vacio"
            )
        }

        //verificar si ya existe un estudiante con esa matricula

        if(repository.studentExistByEnrollment(request.enrollmentNumber)){
            return StudentResponse(
                success = false,
                message = "Ya existe un estudiante con ese numero de matricula"
            )
        }


        val student = repository.createStudent(request)

        return if (student != null){
            StudentResponse(
                success = true,
                message = "Estudiante creado existosamente",
                data = student
            )
        } else {
            StudentResponse(
                success = false,
                message = "Error al crear estudiante"
            )
        }
    }
}