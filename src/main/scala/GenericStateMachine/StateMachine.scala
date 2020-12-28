package GenericStateMachine
import StateMachineOptions._

case class StateMachine[EnumId, InnerState](machineSettings: StateMachineSettings,
                                            nodes: Set[StateMachineNode[EnumId, InnerState]],
                                            currentNode: GenericNode[EnumId, InnerState],
                                            transitionCallback: EnumId => Unit,
                                            possibleTransitions: Map[EnumId, List[InnerState => Option[EnumId]]] = Map())
extends GenericStateMachine[EnumId, InnerState] {

  override def getCurrentId(): Option[EnumId] = {
    if(currentNode.isValid()) {
      Some(currentNode.getId())
    } else {
      None
    }
  }

  override def getCurrentState(): Option[InnerState] = {
    if(currentNode.isValid()) {
      Some(currentNode.getState())
    } else {
      None
    }
  }

  override def flatMap[Id, Inner](f: GenericNode[EnumId, InnerState] => GenericStateMachine[Id, Inner])
    : Option[GenericStateMachine[Id, Inner]] = {
    if(currentNode.canTransition()) {
      Some(f(currentNode))
    } else {
      None
    }
  }

  override def map(f: InnerState => InnerState): Option[GenericStateMachine[EnumId, InnerState]] = {
    val altered = currentNode.alter(f)

    (altered, machineSettings) match {
      case (Some(StateMachineNode(id, state, guard, commonInvariant)),
            StateMachineSettings(ManualTransition, _, preserveStateOnTransition)) => {
        val newNode = StateMachineNode[EnumId, InnerState](id, state, guard, commonInvariant)
        Some(StateMachine(machineSettings, nodes, newNode, transitionCallback, possibleTransitions))
      }

      case (Some(StateMachineNode(id, state, _, _)),
            StateMachineSettings(AutomaticTransition, selectionOption, preserveStateOnTransition)) => {
        val newId = TransitionSelector.select(possibleTransitions.get(id).get, selectionOption,state)
        val transitionNode = nodes.find(n => n.getId() == newId)

        transitionNode match {
          case Some(newNode @ StateMachineNode(id, state, guard, commonInvariant)) => {
            transitionCallback(id)
            val currentState = currentNode.getState()
            val newInitialState: InnerState = if (preserveStateOnTransition) currentState else state
            val newNode = StateMachineNode(id, newInitialState, guard, commonInvariant)
            Some(StateMachine(machineSettings, nodes, newNode, transitionCallback, possibleTransitions))
          }

          case _ => None
        }
      }

      case (_, _) => None
    }
  }

  override def transition(f: (EnumId, InnerState) => Option[EnumId]): Option[GenericStateMachine[EnumId, InnerState]] = {
    if(currentNode.canTransition()) {
      val transitionNode = f(currentNode.getId(), currentNode.getState())
        .flatMap(id => nodes.find(node => node.getId() == id))

      (transitionNode, machineSettings) match {
        case (Some(StateMachineNode(id, state, guard, commonInvariant)),
              StateMachineSettings(_, _, preserveStateOnTransition)) => {
          transitionCallback(id)
          val newState = if (preserveStateOnTransition) currentNode.getState() else state
          val newNode = StateMachineNode(id, newState, guard, commonInvariant)
          Some(StateMachine(machineSettings, nodes,  newNode, transitionCallback, possibleTransitions))
        }

        case (_, _) => None
      }
    } else {
      None
    }
  }
}
