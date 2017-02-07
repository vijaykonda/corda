package net.corda.core.flows

import net.corda.core.crypto.Party
import net.corda.core.crypto.SecureHash
import net.corda.core.node.services.ServiceType
import net.corda.flows.ResolveTransactionsFlow
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

// I needed whole flow metadata stored, later it can be useful with encoding flow backward compatibility etc.
// Problem with flows on our platform is that on incoming connection we can register as an initiator whatever we want.
// With that solution it's easier to hold some data and have control over that process.
interface FlowFactory {
    val preferred: String // TODO defaults
    val deprecated: List<String>
    val counterPartyFlowNames: List<String>
    val toAdvertise: Boolean // We may wish not to advertise that flow in NMS but still register it. //todo think of use case
    val genericFlowName: String
    fun getFlow(version: String, party: Party): FlowLogic<*>?
}

fun majorVersionMatch(version1: String, version2: String): Boolean {
    require(version1.matches(versionRegex) && version2.matches(versionRegex)) { "Incorrect version formats" }
    return (version1.split(".")[0] == version2.split(".")[0])
}

// TODO example
object ResolveTransactionFlowFactory: FlowFactory {
    override val preferred: String = "1.0"
    override val deprecated: List<String> = listOf("0.5")
    override val counterPartyFlowNames: List<String> = listOf(ResolveTransactionsFlow::class.simpleName ?: "")
    override val toAdvertise: Boolean = true
    override val genericFlowName: String = "ResolveTransactionFlow"

    // It's easier that way of having backward compatibility
    override fun getFlow(version: String, party: Party): FlowLogic<*>? {
        return when (version) {
            "1.0" -> ResolveTransactionsFlow(emptySet<SecureHash>(), party) // Can be fully qualified name net.corda....v2.ResolveTransactionFlow or ResolveTransactionFlowV2
            "0.5" -> ResolveTransactionsFlow(emptySet<SecureHash>(), party)
            else -> null
        }
    }
}
