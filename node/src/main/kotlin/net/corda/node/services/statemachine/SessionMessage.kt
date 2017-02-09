package net.corda.node.services.statemachine

import net.corda.core.abbreviate
import net.corda.core.crypto.Party
import net.corda.core.flows.FlowException
import net.corda.core.utilities.UntrustworthyData
import net.corda.core.utilities.debug
import org.slf4j.Logger

interface SessionMessage

data class SessionInit(val initiatorSessionId: Long, val flowName: String, val firstPayload: Any?) : SessionMessage

interface ExistingSessionMessage : SessionMessage {
    val recipientSessionId: Long
}

data class SessionData(override val recipientSessionId: Long, val payload: Any) : ExistingSessionMessage {
    override fun toString(): String {
        return "${javaClass.simpleName}(recipientSessionId=$recipientSessionId, payload=${payload.toString().abbreviate(100)})"
    }
}

interface SessionInitResponse : ExistingSessionMessage {
    val initiatorSessionId: Long
    override val recipientSessionId: Long get() = initiatorSessionId
}
data class SessionConfirm(override val initiatorSessionId: Long, val initiatedSessionId: Long) : SessionInitResponse
data class SessionReject(override val initiatorSessionId: Long, val errorMessage: String) : SessionInitResponse

interface SessionEnd : ExistingSessionMessage
data class NormalSessionEnd(override val recipientSessionId: Long) : SessionEnd
data class ErrorSessionEnd(override val recipientSessionId: Long, val errorResponse: FlowException?) : SessionEnd

data class ReceivedSessionMessage<out M : ExistingSessionMessage>(val sender: Party, val message: M)

fun <T> ReceivedSessionMessage<SessionData>.checkPayloadIs(type: Class<T>, logger: Logger): UntrustworthyData<T> {
    if (type.isInstance(message.payload)) {
        logger.debug { "Received ${message.payload.toString().abbreviate(100)}" }
        return UntrustworthyData(type.cast(message.payload))
    } else {
        throw FlowSessionException("We were expecting a ${type.name} from $sender but we instead got a " +
                "${message.payload.javaClass.name} (${message.payload})")
    }
}

class FlowSessionException(message: String) : RuntimeException(message)
