package github.leavesczy.compose_chat.ui.profile.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

@Stable
data class ProfileUpdatePageViewStata(
    val personProfile: PersonProfile
)