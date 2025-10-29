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

    val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
    println("🚀 Servidor corriendo en puerto $port")
}