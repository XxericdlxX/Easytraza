package cat.copernic.easytraza_mobile.rf07_albarans_ocr.domain

import cat.copernic.easytraza_mobile.comu.data.MobileBackendRepository
import cat.copernic.easytraza_mobile.rf07_albarans_ocr.network.dto.OcrAlbaraResponseDto
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
