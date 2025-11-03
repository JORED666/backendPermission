package core.data

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val scope: String? = null,
    val refresh_token: String? = null
)

@Serializable
data class GoogleUserInfo(
    val id: String,
    val email: String,
    val verified_email: Boolean,
    val name: String,
    val given_name: String,
    val family_name: String,
    val picture: String? = null
)

@Serializable
data class GitHubTokenRequest(
    val client_id: String,
    val client_secret: String,
    val code: String
)

@Serializable
data class GitHubTokenResponse(
    val access_token: String,
    val token_type: String,
    val scope: String
)

@Serializable
data class GitHubUserInfo(
    val id: Long,
    val login: String,
    val name: String?,
    val email: String?,
    val avatar_url: String?
)

@Serializable
data class GitHubEmail(
    val email: String,
    val primary: Boolean,
    val verified: Boolean,
    val visibility: String? = null
)