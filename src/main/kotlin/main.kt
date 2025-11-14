package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.websocket.*
import io.ktor.http.*
import io.github.cdimascio.dotenv.dotenv
import java.time.Duration
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
import history.infrastructure.initHistory
import history.infrastructure.routes.configureHistoryRoutes
import notify.infrastructure.initNotify
import notify.infrastructure.routes.configureNotificationRoutes
import notify.infrastructure.websocket.configureNotificationWebSocket

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val dotenv = dotenv {
        ignoreIfMissing = true
    }
    
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

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val dbConnection = getDBPool()
    
    val userDependencies = initUsers(
        conn = dbConnection,
        googleClientId = dotenv["GOOGLE_CLIENT_ID"] ?: "",
        googleClientSecret = dotenv["GOOGLE_CLIENT_SECRET"] ?: "",
        googleRedirectUrl = dotenv["GOOGLE_REDIRECT_URL"] ?: "http://localhost:8080/api/auth/google/callback",
        githubClientId = dotenv["GITHUB_CLIENT_ID"] ?: "",
        githubClientSecret = dotenv["GITHUB_CLIENT_SECRET"] ?: "",
        githubRedirectUrl = dotenv["GITHUB_REDIRECT_URL"] ?: "http://localhost:8080/api/auth/github/callback",
        frontendUrl = dotenv["FRONTEND_URL"] ?: "http://localhost:5173"
    )

    configureUserRoutes(
        userDependencies.createUserController,
        userDependencies.getAllUsersController,
        userDependencies.getByIdUserController,
        userDependencies.updateUserController,
        userDependencies.deleteUserController,
        userDependencies.authController,
        userDependencies.googleOAuthController,
        userDependencies.gitHubOAuthController
    )

    val tutorDependencies = initTutors(dbConnection)
    configureTutorRoutes(
        tutorDependencies.createTutorController, 
        tutorDependencies.getAllTutorsController, 
        tutorDependencies.getTutorByIdController, 
        tutorDependencies.updateTutorController, 
        tutorDependencies.deleteTutorController
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
        createStudentController = studentDependencies.createStudentController,
        getAllStudentController = studentDependencies.getAllStudentController,
        getStudentByIdController = studentDependencies.getStudentByIdController,
        getStudentByUserIdController = studentDependencies.getStudentByUserIdController, // NUEVO
        getStudentsByTutorIdController = studentDependencies.getStudentsByTutorIdController,
        searchStudentController = studentDependencies.searchStudentController,
        updateStudentController = studentDependencies.updateStudentController,
        deleteStudentController = studentDependencies.deleteStudentController
    )

    val permitsTeacherDependencies = initPermitTeacher(dbConnection)
    configurePermitTeacherRoutes(
        permitsTeacherDependencies.createPermitTeacherController, 
        permitsTeacherDependencies.getAllPermitTeacherController, 
        permitsTeacherDependencies.getPermitTeacherByIdController, 
        permitsTeacherDependencies.deletePermitTeacherController
    )

    val historyDependencies = initHistory(dbConnection)
    configureHistoryRoutes(
        historyDependencies.createHistoryController, 
        historyDependencies.getAllHistoryController, 
        historyDependencies.getHistoryByIdController, 
        historyDependencies.getHistoryByStudentController, 
        historyDependencies.getHistoryByTutorController,
        historyDependencies.updateHistoryStatusController
    )

    val notifyDependencies = initNotify(
        dbConnection,
        studentDependencies.studentRepository,
        permitsTeacherDependencies.permitTeacherRepository,
        tutorDependencies.tutorRepository,
        teacherDependencies.teacherRepository
    )

    configureNotificationRoutes(
        notifyDependencies.getNotificationsController,
        notifyDependencies.markAsReadController
    )

    configureNotificationWebSocket(notifyDependencies.webSocketManager)

    val permitionDependencies = initPermits(
        conn = dbConnection,
        notificationService = notifyDependencies.notificationService,
        studentRepository = studentDependencies.studentRepository  
    )
    
    configurePermitRoutes(
        permitionDependencies.createPermitController, 
        permitionDependencies.getAllPermitsController, 
        permitionDependencies.getPermitByIdController, 
        permitionDependencies.updatePermitController, 
        permitionDependencies.deletePermitController
    )

    val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
    println("🚀 Servidor corriendo en puerto $port")
    println("🔒 OAuth configurado:")
    println("⚙️   - Google: ${dotenv["GOOGLE_REDIRECT_URL"]}")
    println("⚙️   - GitHub: ${dotenv["GITHUB_REDIRECT_URL"]}")
    println("🔔 WebSocket de notificaciones: ws://localhost:$port/ws/notifications/{userId}")
}