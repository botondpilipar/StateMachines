import org.scalatest._
import flatspec._
import matchers._

import GenericStateMachine._
import StateMachineOptions._

object InvariantFactory {
  def equals[A](a: A): (A => Boolean) = (_ == a)
}


class ManualStatemachineTest extends AnyFlatSpec {
  val generalInvariant: (Int => Boolean) = _ % 2 == 0
  val callbackFunction: (EnumIdExample => Unit) = (_ => common.callbackFunctionCalledTimes += 1)
  val mapFunction: (Int => Int) = _ + 2

  def fixture = {
    new {
      val exampleNodeSet = Set[StateMachineNode[EnumIdExample, Int]](
        StateMachineNode(EnumIdFirst, 2, InvariantFactory.equals(4), generalInvariant),
        StateMachineNode(EnumIdSecond, 8, InvariantFactory.equals(6), generalInvariant),
        StateMachineNode(EnumIdThird, 10, InvariantFactory.equals(8), generalInvariant)
      )
      val settings = StateMachineSettings(ManualTransition, FirstMatches, preserveStateOnTransition = true)
      var callbackFunctionCalledTimes = 0
      val evenStateMachine = StateMachine(settings,
        exampleNodeSet,
        exampleNodeSet.head,
        transitionCallback = callbackFunction,
        Map())
    }
  }

  val common = fixture


  "getCurrentState" should "return underlying node's inner state" in {
    assert(common.evenStateMachine.getCurrentState().nonEmpty)
    assertResult(2)(common.evenStateMachine.getCurrentState().get)
  }

  "getCurrentId" should "return underlying node's current id" in {
    assert(common.evenStateMachine.getCurrentId().nonEmpty)
    assertResult(EnumIdFirst)(common.evenStateMachine.getCurrentId().get)
  }

  "map" should "alter underlying node's state" in {
    val mappedSM = common.evenStateMachine.map(mapFunction)
    assert(mappedSM.nonEmpty)
    assertResult(4)(mappedSM.get.getCurrentState().get)
  }

  "map" should "return none if mapping function made invariant false" in {
    val mappedSM = common.evenStateMachine.map(i => i + 1)
    assert(mappedSM.isEmpty)
  }

  "transition" should "replace id with state changed as configured" in {
    val machine = StateMachine(common.settings, common.exampleNodeSet, common.exampleNodeSet.head, callbackFunction, Map())
    val enumIdSecondTransition = machine.map(mapFunction)
                                        .flatMap(m => m.transition(TransitionGraphExample.transition12))
    assert(enumIdSecondTransition.nonEmpty)
    assertResult(4)(enumIdSecondTransition.get.getCurrentState().get)
    assertResult(EnumIdSecond)(enumIdSecondTransition.get.getCurrentId().get)
  }

  "transition" should "reset state if preserveState setting is false" in {
    val settings = StateMachineSettings(ManualTransition, FirstMatches, false)
    val machine = StateMachine(settings, common.exampleNodeSet, common.exampleNodeSet.head, callbackFunction, Map())

    val enumIdSecondTransition = machine.map(mapFunction)
      .flatMap(m => m.transition(TransitionGraphExample.transition12))
    assert(enumIdSecondTransition.nonEmpty)
    assertResult(EnumIdSecond)(enumIdSecondTransition.get.getCurrentId().get)
    assertResult(8)(enumIdSecondTransition.get.getCurrentState().get)
  }

  "transition" should "return none if transition function result is none" in {
    val enumIdSecondMachine = common.evenStateMachine.map(mapFunction)
      .flatMap(m => m.transition(TransitionGraphExample.transition12))
      .flatMap(m => m.map(mapFunction))
      .flatMap(m => m.transition(TransitionGraphExample.transition23))
      .flatMap(m => m.map(mapFunction))

    assert(enumIdSecondMachine.nonEmpty)
    assert(enumIdSecondMachine.flatMap(m => m.transition(TransitionGraphExample.transition3)).isEmpty)
  }
}

