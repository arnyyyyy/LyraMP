package com.arno.lyramp.feature.authorization.presentation

class AuthUpdateHandler {

        fun handle(
                state: AuthState,
                update: AuthUpdate
        ): AuthCommand {
                return when (update) {
                        AuthUpdate.Loading ->
                                AuthCommand(
                                        state = state.copy(isLoading = true, error = null)
                                )

                        is AuthUpdate.Error ->
                                AuthCommand(
                                        state = state.copy(
                                                isLoading = false,
                                                error = update.message
                                        )
                                )

                        AuthUpdate.Finish ->
                                AuthCommand(
                                        state = state.copy(isLoading = false)
                                )

                        AuthUpdate.SuccessNavigate ->
                                AuthCommand(
                                        state = state.copy(isLoading = false),
                                        news = AuthNews.NavigateToHistory
                                )
                }
        }
}
