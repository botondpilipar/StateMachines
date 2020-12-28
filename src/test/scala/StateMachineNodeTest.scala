import org.scalatest._
import flatspec._
import matchers._

import GenericStateMachine._



class StateMachineNodeTest extends AnyFlatSpec {
  val constTrueInt: (Int => Boolean) = (_ => true)
  val constFalseInt: (Int => Boolean) = (_ => false)

  val falseGuardNode = StateMachineNode(EnumIdFirst, 1, constFalseInt, constTrueInt)
  val falseInvariantNode = StateMachineNode(EnumIdFirst, 1, constTrueInt, constFalseInt)
  val falseBothNode = StateMachineNode(EnumIdFirst, 1, constFalseInt, constFalseInt)
  val trueBothNode = StateMachineNode(EnumIdFirst, 1, constTrueInt, constTrueInt)
  val evenNode = StateMachineNode(EnumIdFirst, 2, (i: Int) => i == 6, (i: Int) => i % 2 == 0)

  it should "Return its original state" in {
    println("Test start")
    println("No infinite recursion yet")
    assertResult(1)(trueBothNode.getState())
  }

  it should "Detect transition change according to transitionFunction" in {
    assert(trueBothNode.canTransition())
    assertResult(false)(falseGuardNode.canTransition())
  }

  it should "Detect validity according to invariant" in {
    assertResult(false)(falseBothNode.isValid())
    assertResult(true)(falseGuardNode.isValid())
  }

  it should "Detect transition change according to transition function and validity" in {
    assert(! falseGuardNode.canTransition())
    assert(! falseInvariantNode.canTransition())
    assert(! falseBothNode.canTransition())
  }

  it should "Return altered stated Node when alter is called" in {
    val alteredNode = trueBothNode.alter(a => a + 1)
    assert(alteredNode.nonEmpty)
    assertResult(2)(alteredNode.get.getState())
  }

  "Alter function" should "Return none if altered state does not satisfy invariant" in {
    val node = StateMachineNode(EnumIdFirst, 2, (i: Int) => i == 6, (i: Int) => i % 2 == 0)
    val altered = node.alter(a => a + 1)
    assert(altered.isEmpty)
  }

  "Alter function" should "Change transition function result" in {
    val alterFunction: (Int => Int) = _ + 4
    val altered = evenNode.alter(alterFunction)

    assert(altered.nonEmpty)
    assert(altered.get.canTransition())
  }

  "Alter function" should "Change invariant function result" in {
    val alterFunction: (Int => Int) = _ + 1
    val node = evenNode.alter(alterFunction)
    assert(node.isEmpty)
  }
}
