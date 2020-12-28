package GenericStateMachine

case class StateMachineNode[EnumId, InnerState](id: EnumId,
                                                  state: InnerState,
                                                  guard: InnerState => Boolean,
                                                  commonInvariant: InnerState => Boolean)
    extends GenericNode[EnumId, InnerState] {

    override def canTransition(): Boolean = commonInvariant(state) && guard(state)

    override def alter(f: InnerState => InnerState): Option[GenericNode[EnumId, InnerState]] = {
      val altered = f(state)
      val invariantHoldsTrue = commonInvariant(altered)
      if(invariantHoldsTrue) {
        return Some(StateMachineNode(id, altered, guard, commonInvariant))
      } else {
        return None;
      }
    }

    override def getState(): InnerState = state

  override def getId(): EnumId = id

    override def isValid(): Boolean = commonInvariant(state)
  }

