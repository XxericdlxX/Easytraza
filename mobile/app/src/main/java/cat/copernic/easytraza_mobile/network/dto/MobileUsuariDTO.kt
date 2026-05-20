package cat.copernic.easytraza_mobile.network.dto

data class MobileUsuariDto(
    val id: Long,
    val nom: String? = null,
    val cognoms: String? = null,
    val rol: String? = null,

    val fotoPerfilUrl: String? = null
)
