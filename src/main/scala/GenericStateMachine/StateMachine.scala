package GenericStateMachine
import StateMachineOptions._

case class StateMachine[EnumId, InnerState](machineSettings: StateMachineSettings,
                                                    nodes: Set[GenericNode[EnumId, InnerState]],
                                                    currentState: GenericNode[EnumId, InnerState],
                                                    transitionCallback: EnumId => Unit,
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

  override def map(f: InnerState => InnerState): Option[GenericStateMachine[EnumId, InnerState]] = {
    val altered = currentState.alter(f)
    if(altered.nonEmpty && machineSettings.transitionOption == ManualTransition) {

      Some(StateMachine(machineSettings, nodes, altered.get, transitionCallback,  possibleTransitions))

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
      Some(StateMachine(machineSettings, nodes,  nodes.find(node => node.getId() == newId).get, transitionCallback, possibleTransitions))
    } else {
      None
    }
  }
}

object StateMachine {
  def apply[EnumId, InnerState](machineSettings: StateMachineSettings,
                                nodes: Set[GenericNode[EnumId, InnerState]],
                                transitionCallback: Unit => Unit,
                                first: GenericNode[EnumId, InnerState]): GenericStateMachine[EnumId, InnerState] =
    StateMachine[EnumId, InnerState](machineSettings, nodes, transitionCallback, first)
}
