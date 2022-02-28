package github.leavesczy.compose_chat.logic

import github.leavesczy.compose_chat.base.provider.*
import github.leavesczy.compose_chat.proxy.logic.*

/**
 * @Author: leavesCZY
 * @Date: 2021/6/22 11:35
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object ComposeChat {

    const val groupIdA = "@TGS#3SSMB3WHI"

    const val groupIdB = "@TGS#3VOZA3WHT"

    const val groupIdC = "@TGS#3W42A3WHP"

    const val groupIdToUploadAvatar = "@TGS#aZRGY4WHQ"

    val accountProvider: IAccountProvider = AccountProvider()

    val conversationProvider: IConversationProvider = ConversationProvider()

    val messageProvider: IMessageProvider = MessageProvider()

    val friendshipProvider: IFriendshipProvider = FriendshipProvider()

    val groupProvider: IGroupProvider = GroupProvider()

}