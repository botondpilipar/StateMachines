package GenericStateMachine

case class StateMachineNode[EnumId, InnerState](id: EnumId,
                                                state: InnerState,
                                                guard: InnerState => Boolean,
                                                commonInvariant: InnerState => Boolean,
                                                nextNode: EnumId)
  extends GenericNode[EnumId, InnerState] {

  override def canTransition(innerState: InnerState): Boolean = guard(state)

  override def alter(f: InnerState => InnerState): Option[GenericNode[EnumId, InnerState]] = {
    val altered = f(state)
    val invariantHoldsTrue = commonInvariant(altered)
    if(invariantHoldsTrue) {
      return Some(StateMachineNode(id, altered, guard, commonInvariant, nextNode))
    } else {
      return None;
    }
  }

  override def getState(): InnerState = state

  override def transitionTo(): Option[EnumId] = {
    if(guard(state)) {
      return Some(nextNode)
    } else {
      return None;
    }
  }
}
