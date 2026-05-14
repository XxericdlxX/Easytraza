package cat.copernic.easytraza_mobile.network.dto

data class OcrLotRespostaDto(
    val codiLot: String? = null,
    val materiaPrima: String? = null,
    val quantitat: Double? = null
)

data class OcrAlbaraResponseDto(
    val proveidorCif: String? = null,
    val numeroAlbara: String? = null,
    val dataAlbara: String? = null,
    val textDetectat: String? = null,
    val lots: List<OcrLotRespostaDto> = emptyList()
)

data class MobileLotSaveRequestDto(
    val codiLot: String,
    val quantitat: Double?,
    val materiaPrimaNom: String,
    val crearMateriaPrimaSiNoExisteix: Boolean
)

data class MobileAlbaraSaveRequestDto(
    val dataRecepcio: String,
    val proveidorCif: String?,
    val proveidorNom: String,
    val crearProveidorSiNoExisteix: Boolean,
    val lots: List<MobileLotSaveRequestDto>
)