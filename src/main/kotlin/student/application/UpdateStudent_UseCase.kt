

class UpdateStudent_UseCase (private val repository: StudentRepository){

    suspend operator fun invoke (studentId: Int, request: UpdateStudentRequest): StudentResponse{

        //verificar que el estudiante exista
        val existingStudent = repository.getStudentById(studentId)
        if (existingStudent == null){
            success = false,
            message = "Estudiante no encontrado"
        }
    }

    //validar que la nueva matricula no este en uso(si se esta actualizando)

    request.enrollmentNumber?.let {newEnrollment ->
        if (enrollmentNumber.isNotBlank() && newEnrollment != existingStudent.enrollmentNumber){
            if (repository.studentExistByEnrollment(enrollmentNumber = newEnrollment)){
                return StudentResponse(
                    success = false,
                    message = "Ya existe un estudiante con ese numero de matricula"
                )
            }
        }
    }

    val updated = repository.updateStudent(studentId, request)

    return if (updated){
        val updatedStudent = repository.getStudentById(studentId)
        StudentResponse(
            success = true,
            message = "Estudiante actualizado exitosamente",
            data = UpdatedStudent
        )
    } else {
        StudentResponse(
            success = false,
            message = "Error al actualizar el estudiante"
        )
    }




}