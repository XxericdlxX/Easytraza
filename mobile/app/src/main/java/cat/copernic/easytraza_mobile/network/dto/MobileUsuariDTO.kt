package cat.copernic.easytraza_mobile.network.dto

data class MobileUsuariDto(
    val id: Long,
    val nom: String? = null,
    val cognoms: String? = null,
    val email: String? = null,
    val rol: String? = null
)
