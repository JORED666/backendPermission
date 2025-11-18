package core.pdf

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.io.font.constants.StandardFonts
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate

class PDFGeneratorService {
    
    companion object {
        private val MESES = listOf(
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        )
        
        private val REASON_TRANSLATIONS = mapOf(
            "Family" to "Familiar",
            "Health" to "Salud",
            "Economic" to "Económico",
            "Academic Events" to "Eventos Académicos",
            "Sports" to "Deportes",
            "Pregnancy" to "Embarazo",
            "Accidents" to "Accidente",
            "Addictions" to "Adiccion",
            "Personal Procedures" to "Trámites Personales",
            "Other" to "Otro"
        )
        
        fun generatePermitPDF(
            studentName: String,
            matricula: String,
            startDate: LocalDate,
            endDate: LocalDate,
            reason: String,
            tutorName: String,
            templateImagePath: String = "src/main/resources/template/template.png"
        ): ByteArray {
            val outputStream = ByteArrayOutputStream()
            
            val writer = PdfWriter(outputStream)
            val pdfDoc = PdfDocument(writer)
            
            // 🎯 PDF DEL TAMAÑO EXACTO DE LA IMAGEN (no más grande)
            val document = Document(pdfDoc, PageSize.A4)
            
            document.setMargins(0f, 0f, 0f, 0f)
            
            try {
                // 1. Agregar imagen de fondo (tamaño exacto A4)
                val templateFile = File(templateImagePath)
                if (templateFile.exists()) {
                    val imageData = ImageDataFactory.create(templateImagePath)
                    val backgroundImage = Image(imageData)
                    backgroundImage.setFixedPosition(0f, 0f)
                    // 🎯 Ajustar exactamente al tamaño A4
                    backgroundImage.scaleToFit(PageSize.A4.width, PageSize.A4.height)
                    document.add(backgroundImage)
                }
                
                val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
                val normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
                
                val fechaActual = formatearFecha(LocalDate.now())
                
                // 2. Header derecho - MÁS ABAJO y en NEGRITAS
                val headerBlock = Paragraph()
                    .add(Text("INGENIERÍA EN SOFTWARE\n").setFont(boldFont).setFontSize(10f))
                    .add(Text("Suchiapa, Chiapas. $fechaActual").setFont(boldFont).setFontSize(10f))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFixedPosition(
                        PageSize.A4.width - 220f,
                        PageSize.A4.height - 170f, // 🎯 MÁS ABAJO
                        200f
                    )
                document.add(headerBlock)
                
                // 3. Destinatarios - MÁS A LA DERECHA
                val destinatarios = Paragraph()
                    .add("PTC'S Y PA'S\n")
                    .add("Ingeniería en Software\n")
                    .add("Universidad Politécnica de Chiapas")
                    .setFont(boldFont)
                    .setFontSize(10f)
                    .setFixedPosition(
                        130f, // 🎯 MÁS A LA DERECHA
                        PageSize.A4.height - 280f,
                        300f
                    )
                document.add(destinatarios)
                
                // 4. Cuerpo del texto - MÁS A LA DERECHA
                val mes = obtenerMes(startDate)
                val reasonEspanol = REASON_TRANSLATIONS[reason] ?: reason
                
                val cuerpo = Paragraph()
                    .add(Text("Por medio de la presente me dirijo a usted de la manera más atenta, para hacer de su conocimiento que el C. ").setFont(normalFont).setFontSize(10f))
                    .add(Text(studentName).setFont(boldFont).setFontSize(10f))
                    .add(Text(" estudiante de la carrera de Ingeniería en Software con matrícula ").setFont(normalFont).setFontSize(10f))
                    .add(Text(matricula).setFont(boldFont).setFontSize(10f))
                    .add(Text(", no se presentará a clases del ").setFont(normalFont).setFontSize(10f))
                    .add(Text("${startDate.dayOfMonth} - ${endDate.dayOfMonth}").setFont(boldFont).setFontSize(10f))
                    .add(Text(" de $mes del año en curso, por temas de ").setFont(normalFont).setFontSize(10f))
                    .add(Text(reasonEspanol).setFont(boldFont).setFontSize(10f))
                    .add(Text("; por lo que solicito de ustedes su apoyo para recibirle tareas, así como otras actividades realizadas en su ausencia.").setFont(normalFont).setFontSize(10f))
                    .setTextAlignment(TextAlignment.JUSTIFIED)
                    .setFixedPosition(
                        130f, // 🎯 MÁS A LA DERECHA
                        PageSize.A4.height - 480f,
                        PageSize.A4.width - 190f // 🎯 Ancho ajustado
                    )
                document.add(cuerpo)
                
                // 5. Despedida - MÁS A LA DERECHA
                val despedida = Paragraph("Sin más por el momento, le reitero mis más cordiales saludos.")
                    .setFont(normalFont)
                    .setFontSize(10f)
                    .setFixedPosition(
                        130f, // 🎯 MÁS A LA DERECHA
                        PageSize.A4.height - 560f,
                        400f
                    )
                document.add(despedida)
                
                // 6. ATENTAMENTE - MÁS A LA DERECHA
                val atentamente = Paragraph("ATENTAMENTE")
                    .setFont(boldFont)
                    .setFontSize(10f)
                    .setFixedPosition(
                        130f, // 🎯 MÁS A LA DERECHA
                        PageSize.A4.height - 650f,
                        300f
                    )
                document.add(atentamente)
                
                // 7. Lema - MÁS A LA DERECHA
                val lema = Paragraph("\"TECNOLOGÍA PARA EL BIEN COMÚN\"")
                    .setFont(boldFont)
                    .setFontSize(8f)
                    .setFixedPosition(
                        130f, // 🎯 MÁS A LA DERECHA
                        PageSize.A4.height - 670f,
                        300f
                    )
                document.add(lema)
                
                // 8. Texto "Firma" - MÁS A LA DERECHA
                val firmaTexto = Paragraph("Firma")
                    .setFont(boldFont)
                    .setFontSize(32f)
                    .setFontColor(DeviceRgb(29, 78, 216))
                    .setRotationAngle(Math.toRadians(-12.0))
                    .setFixedPosition(
                        130f, // 🎯 MÁS A LA DERECHA
                        200f,
                        200f
                    )
                document.add(firmaTexto)
                
                // 9. Datos del tutor - MÁS A LA DERECHA
                val tutorParagraph = Paragraph()
                    .add(Text("Mtro. $tutorName\n").setFont(boldFont).setFontSize(10f))
                    .add(Text("Tutor\n").setFont(boldFont).setFontSize(9f))
                    .add(Text("Ingeniería en Software").setFont(boldFont).setFontSize(9f))
                    .setBorderTop(com.itextpdf.layout.borders.SolidBorder(1f))
                    .setPaddingTop(8f)
                    .setWidth(288f)
                    .setFixedPosition(
                        130f, // 🎯 MÁS A LA DERECHA
                        120f,
                        288f
                    )
                    
                document.add(tutorParagraph)
                
            } finally {
                document.close()
            }
            
            return outputStream.toByteArray()
        }
        
        private fun formatearFecha(fecha: LocalDate): String {
            val mes = MESES[fecha.monthValue - 1]
            return "${fecha.dayOfMonth} de $mes de ${fecha.year}"
        }
        
        private fun obtenerMes(fecha: LocalDate): String {
            return MESES[fecha.monthValue - 1]
        }
    }
}