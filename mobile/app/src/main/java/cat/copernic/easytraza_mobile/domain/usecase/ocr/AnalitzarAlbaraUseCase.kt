package cat.copernic.easytraza_mobile.domain.usecase.ocr

import cat.copernic.easytraza_mobile.data.repository.MobileBackendRepository
import cat.copernic.easytraza_mobile.network.dto.OcrAlbaraResponseDto
import okhttp3.MultipartBody

/**
 * Cas d'ús que envia un albarà al backend perquè sigui analitzat per OCR.
 */
class AnalitzarAlbaraUseCase(
    private val repository: MobileBackendRepository = MobileBackendRepository()
) {

    /**
     * Processa el document OCR contra el backend configurat.
     * @param serverHost host o IP del servidor.
     * @param fitxer document multipart seleccionat o capturat al dispositiu.
     * @return dades OCR retornades pel backend.
     */
    suspend operator fun invoke(
        serverHost: String,
        fitxer: MultipartBody.Part
    ): OcrAlbaraResponseDto {
        return repository.analitzarAlbara(serverHost, fitxer)
    }
}
