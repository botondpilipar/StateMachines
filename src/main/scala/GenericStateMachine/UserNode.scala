package GenericStateMachine

case class UserNode[EnumId, InnerState](id: EnumId,
                                       state: InnerState,
                                       guard: InnerState => Boolean,
                                       nextNode: EnumId) {
  def apply(id: EnumId,
            state: InnerState,
            guard: InnerState => Boolean,
            nextNode: EnumId): UserNode[EnumId, InnerState] = UserNode(id, state, guard, nextNode)
}
