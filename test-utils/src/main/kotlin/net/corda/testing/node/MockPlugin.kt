package net.corda.testing.node

import net.corda.core.crypto.SecureHash
import net.corda.core.node.CordaPluginRegistry
import net.corda.core.node.PluginServiceHub
import net.corda.flows.ResolveTransactionsFlow
import java.util.function.Function

// TODO predefined registration of flows for tests.
//  Think more of it, because now it's tape and glue.
class MockPlugin : CordaPluginRegistry() {
    override val servicePlugins = listOf(Function(MockService::Service))
}

object MockService {
    class Service(services: PluginServiceHub) {
        init {
            services.registerFlowInitiator(ResolveTransactionsFlow::class, { ResolveTransactionsFlow(emptySet<SecureHash>(), it) }) //todo
        }
    }
}



////todo sketch
//interface ResolveTransactionFlowFactory {
//    //        override fun registerFlowInitiator(markerClass: KClass<*>, version: String, flowFactory: (Party) -> FlowLogic<*>) {
//    fun getProtocol(version:String): (Set<SecureHash>, Party) -> FlowLogic<*> { // TODO - return ResolveTrasnactionsFlow
//        // TODO possible different combinations and backward compatibility handling, say we got 1.2 and it's backward comp with 1.3 that we handle, etc.
//        return when (version) {
//            DEFAULT_VERSION -> (::ResolveTransactionsFlow)
//            else -> throw IllegalArgumentException("Unsupported flow version")
//        }
//    }
//}