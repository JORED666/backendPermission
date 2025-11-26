package permition.infrastructure.controllers

import core.cloudinary.CloudinaryService
import core.pdf.PDFGeneratorService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import permition.application.GetPermitByIdWithDetailsUseCase
import permition.application.UpdatePermitDocumentUrlUseCase
import permition.domain.dto.ErrorResponse

class GeneratePermitPDFController(
        private val getPermitByIdWithDetails: GetPermitByIdWithDetailsUseCase,
        private val updatePermitDocumentUrl: UpdatePermitDocumentUrlUseCase
) {

    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            println("📄 Buscando permiso #$id...")

            val permit = getPermitByIdWithDetails.execute(id)

            if (permit == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Permiso no encontrado"))
                return
            }

            println("✅ Permiso encontrado: ${permit.studentInfo.fullName}")

            if (permit.status.name.lowercase() != "approved") {
                call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("El permiso debe estar aprobado para generar el documento")
                )
                return
            }

            if (!permit.permitDocumentUrl.isNullOrBlank()) {
                println("ℹ️ El documento ya existe: ${permit.permitDocumentUrl}")
                call.respond(
                        HttpStatusCode.OK,
                        mapOf("message" to "Documento ya existe", "url" to permit.permitDocumentUrl)
                )
                return
            }

            println("📄 Generando PDF para permiso #$id...")

            val pdfBytes =
                    PDFGeneratorService.generatePermitPDF(
                            studentName = permit.studentInfo.fullName,
                            matricula = permit.studentInfo.enrollmentNumber ?: "Sin matrícula",
                            startDate = permit.startDate,
                            endDate = permit.endDate,
                            reason = permit.reason.displayName,
                            tutorName = permit.tutorInfo.fullName,
                            firmaUrl = permit.tutorInfo.firmaUrl
                    )

            println("✅ PDF generado: ${pdfBytes.size / 1024} KB")

            println("☁️ Subiendo a Cloudinary...")
            val cloudinaryUrl =
                    CloudinaryService.uploadFile(
                            fileBytes = pdfBytes,
                            folder = "permits/documents",
                            fileName = "permit-$id.pdf"
                    )

            println("✅ URL generada: $cloudinaryUrl")

            println("💾 Guardando URL en base de datos...")
            updatePermitDocumentUrl.execute(id, cloudinaryUrl)

            println("✅ URL guardada correctamente")

            call.respond(
                    HttpStatusCode.OK,
                    mapOf("message" to "Documento generado exitosamente", "url" to cloudinaryUrl)
            )
        } catch (error: Exception) {
            println("❌ Error generando PDF: ${error.message}")
            error.printStackTrace()
            call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error.message ?: "Error al generar el documento")
            )
        }
    }
}
