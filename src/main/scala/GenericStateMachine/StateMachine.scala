package GenericStateMachine
import StateMachineOptions._

case class StateMachine[EnumId, InnerState](machineSettings: StateMachineSettings,
                                                    nodes: Set[GenericNode[EnumId, InnerState]],
                                                    commonInvariant: InnerState => Boolean,
                                                    transitionCallback: EnumId => Unit,
                                                    currentState: GenericNode[EnumId, InnerState],
                                                    possibleTransitions: Map[EnumId, (EnumId, InnerState) => EnumId] = Map())
extends GenericStateMachine[EnumId, InnerState] {

  override def getCurrentState(): Option[EnumId] = {
    if(currentState.isValid()) {
      return Some(currentState.getId())
    } else {
      return None
    }
  }

  override def flatMap[Id, Inner](f: GenericNode[EnumId, InnerState] => GenericStateMachine[Id, Inner])
    : Option[GenericStateMachine[Id, Inner]] = {
    if(currentState.canTransition()) {
      Some(f(currentState))
    } else {
      None
    }
  }

  override def mapInner(f: InnerState => InnerState): Option[GenericStateMachine[EnumId, InnerState]] = {
    val altered = currentState.alter(f)
    if(altered.nonEmpty && machineSettings.transitionOption == ManualTransition) {

      Some(StateMachine(machineSettings, nodes, commonInvariant, transitionCallback, altered.get))

    } else if (altered.nonEmpty && machineSettings.transitionOption == AutomaticTransition) {

      val id = altered.get.getId()
      val state = altered.get.getState()
      val transitionFunction = possibleTransitions.get(id).get
      transitionCallback(transitionFunction(id, state))
      transition(transitionFunction)
    } else {
      None
    }
  }

  override def transition(f: (EnumId, InnerState) => EnumId): Option[GenericStateMachine[EnumId, InnerState]] = {
    if(currentState.canTransition()) {
      val newId = f(currentState.getId(), currentState.getState())
      transitionCallback(newId)
      Some(StateMachine(machineSettings, nodes, commonInvariant, transitionCallback, nodes.find(node => node.getId() == newId).get))
    } else {
      None
    }
  }
}

object StateMachine {
  def apply[EnumId, InnerState](nodes: Set[GenericNode[EnumId, InnerState]],
                                commonInvariant: InnerState => Boolean,
                                transitionCallback: Unit => Unit): GenericStateMachine[EnumId, InnerState] =
    StateMachine[EnumId, InnerState](nodes, commonInvariant, transitionCallback)
}
