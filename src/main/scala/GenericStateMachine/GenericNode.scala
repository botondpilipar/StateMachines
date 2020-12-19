package GenericStateMachine

trait GenericNode[EnumId, InnerState] {
  def canTransition(): Boolean

  def alter(f: InnerState => InnerState): Option[GenericNode[EnumId, InnerState]]

  def getState(): InnerState

  def getId(): EnumId

  def isValid(): Boolean
}
