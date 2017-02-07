package net.corda.core.flows

import kotlinx.support.jdk8.collections.merge
import net.corda.core.node.CordaPluginRegistry
import net.corda.core.node.ServiceEntry
import net.corda.core.node.services.ServiceType

// TODO REFACTOR my old approach
class FlowVersionInfo(
        val genericFlowName: String,
        val preferred: String, // With default genericFlowName/ highest version
        val deprecated: List<String> = emptyList(),
        val advertise: Boolean = true // Do we want to advertise this flow.
) {
    init {
        require(toFullList().all{ it.matches(versionRegex) })
    }

    fun toFullList(): List<String> {
        return deprecated + preferred
    }

    //todo for each service/plugin will be different advertised list, so no need for plus
    // or there is a need for that (if we register 2 plugins on the same name)
    // todo think of it after change, it also checked for service it belongs to
    @Throws(IllegalArgumentException::class)
    operator fun plus(other: FlowVersionInfo): FlowVersionInfo {
        val newPreferred = listOf(this.preferred, other.preferred).sorted() //todo
        if (this.genericFlowName == other.genericFlowName)
            return FlowVersionInfo(
                    this.genericFlowName,
                    newPreferred[0], //todo what if both preferred the same
                    this.deprecated + other.deprecated + newPreferred[1],
                    this.advertise && other.advertise //todo advertise
            )
        else
            throw IllegalArgumentException("Cannot merge two different FlowVersionInfo entries.")
    }

    fun toAdvertisedFlows(): AdvertisedFlow? {
        if (advertise)
            return AdvertisedFlow(genericFlowName, preferred, deprecated)
        else return null
    }
}

// Flow versions that will be advertised through NetworkMapService.
data class AdvertisedFlow(
        val genericFlowName: String,
        val preferred: String, // Default highest version. TODO
        val deprecated: List<String> = emptyList() // Flows we still support as new incoming communication.
) {
    init {
        require(toList().all{ it.matches(versionRegex) })
    }

    fun toList(): List<String> {
        return deprecated + preferred
    }

//    // todo think of it, maybe just force ordering when creating advertised flows
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other?.javaClass != javaClass) return false
//        other as AdvertisedFlow
//        if (genericFlowName != other.genericFlowName) return false
//        if (toList().sorted() != other.toList().sorted()) return false
//        return true
//    }
//
//    override fun hashCode(): Int = type.hashCode()
//    override fun toString(): String = type.toString()
}
