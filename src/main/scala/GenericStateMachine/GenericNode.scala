package GenericStateMachine

trait GenericNode[EnumId, InnerState] {
  def canTransition(innerState: InnerState): Boolean
  def alter(f: InnerState => InnerState): Option[GenericNode[EnumId, InnerState]]
  def getState(): InnerState
  def transitionTo(): Option[EnumId]
}
