package net.corda.node.services

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.crypto.Party
import net.corda.core.flows.FlowLogic
import net.corda.core.utilities.DUMMY_NOTARY
import net.corda.testing.ALICE
import net.corda.testing.DefaultFlowVersion

class FlowVersionsTests {

    // todo make receiver and sender flow tests
    // todo test of flow like cashFlow
    // todo make flow that communicates with 2 parties (counterpartyMarker override)
    // todo flow with subflow
    // todo incompatibile fows
    // todo flows with different versions but one is backward comaptible
    // todo registration in plugin services
    // todo tests of advertising
    // todo real life flow test (twoParty? or IRS)
    // todo node start tests
    // todo session management tests
    // todo sth like InMemoryMessagingTests -> topic validation, version validation
    // todo distributed service - different versions advertised
    // todo parse version format

    // TODO tests of different kind of flows
    private class SendFlow(val otherParty: Party, val payload: Any) : DefaultFlowVersion<Unit>() {
        @Suspendable
        override fun call() = send(otherParty, payload)
    }

    private class SendFlowWithManyParties(val payload: Any, vararg val otherParties: Party) : DefaultFlowVersion<Unit>() {
        init {
            require(otherParties.isNotEmpty())
        }

        override fun getCounterpartyMarker(party: Party): String {
            when(party) {
                DUMMY_NOTARY -> TODO()
                ALICE -> TODO()
                else -> TODO()
            } // TODO
        }
        @Suspendable
        override fun call() = otherParties.forEach { send(it, payload) }
    }

    private class ReceiveFlowV1(val otherParty: Party) : FlowLogic<Any>() {
        override val version = "1.0"
        override val genericName = "ReceiveFlow"
        override val preference = listOf(version)

        @Suspendable
        override fun call() = receive<Any>(otherParty).unwrap { it }
    }

    private class ReceiveFlowV11(val otherParty: Party) : FlowLogic<Any>() {
        override val version = "1.1"
        override val genericName = "ReceiveFlow"
        override val preference = listOf(version, "1.1", "1.5")

        @Suspendable
        override fun call() = receive<Any>(otherParty).unwrap { it }
    }

    private class ReceiveFlowV2(val otherParty: Party) : FlowLogic<Any>() {
        override val version = "2.0"
        override val genericName = "ReceiveFlow"
        override val preference = listOf(version, "2.718", "1.5")

        @Suspendable
        override fun call() = receive<Any>(otherParty).unwrap { it }
    }
    // todo flowFactory and registration
}
