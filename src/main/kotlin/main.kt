package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import core.getDBPool
import users.infrastructure.initUsers
import users.infrastructure.routes.configureUserRoutes
import tutors.infrastructure.initTutors
import tutors.infrastructure.routes.configureTutorRoutes
import permition.infrastructure.initPermits
import permition.infrastructure.routes.configurePermitRoutes
import teachers.infrastructure.initTeachers
import teachers.infrastructure.routes.configureTeacherRoutes
import students.infrastructure.initStudents
import students.infrastructure.routes.configureStudentRoutes
import permitsTeacher.infrastructure.initPermitTeacher
import permitsTeacher.infrastructure.routes.configurePermitTeacherRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Options)
        
        allowHeader(HttpHeaders.Origin)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        
        allowCredentials = true
        anyHost()
    }

    install(ContentNegotiation) {
        json()
    }

    val dbConnection = getDBPool()
    val userDependencies = initUsers(dbConnection)

    configureUserRoutes(
        userDependencies.createUserController,
        userDependencies.getAllUsersController,
        userDependencies.getByIdUserController,
        userDependencies.updateUserController,
        userDependencies.deleteUserController,
        userDependencies.authController
    )

    val tutorDependencies = initTutors(dbConnection)
    configureTutorRoutes(
        tutorDependencies.createTutorController, 
        tutorDependencies.getAllTutorsController, 
        tutorDependencies.getTutorByIdController, 
        tutorDependencies.updateTutorController, 
        tutorDependencies.deleteTutorController
    )

    val permitionDependencies = initPermits(dbConnection)
    configurePermitRoutes(
        permitionDependencies.createPermitController, 
        permitionDependencies.getAllPermitsController, 
        permitionDependencies.getPermitByIdController, 
        permitionDependencies.updatePermitController, 
        permitionDependencies.deletePermitController
    )

    val teacherDependencies = initTeachers(dbConnection)
    configureTeacherRoutes(
        teacherDependencies.createTeacherController, 
        teacherDependencies.getAllTeacherController, 
        teacherDependencies.getTeacherByIdController, 
        teacherDependencies.updateTeacherController, 
        teacherDependencies.deleteTeacherController
    )

    val studentDependencies = initStudents(dbConnection)
    configureStudentRoutes(
        studentDependencies.createStudentController, 
        studentDependencies.getAllStudentController, 
        studentDependencies.getStudentByIdController, 
        studentDependencies.searchStudentController, 
        studentDependencies.updateStudentController, 
        studentDependencies.deleteStudentController
    )

    val permitsTeacher = initPermitTeacher(dbConnection)
    configurePermitTeacherRoutes(
        permitsTeacher.createPermitTeacherController, 
        permitsTeacher.getAllPermitTeacherController, 
        permitsTeacher.getPermitTeacherByIdController, 
        permitsTeacher.deletePermitTeacherController
    )

    val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
    println("🚀 Servidor corriendo en puerto $port")
}