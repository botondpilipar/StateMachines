package GenericStateMachine

trait GenericNode[EnumId, InnerState] {

  /**
   * @return true if node is ready to transition, false otherwise
   */
  def canTransition(): Boolean

  /**
   * Map inner node state with function
   * @param f Map operation
   * @return new Node if conditions hold true, otherwise None
   */
  def alter(f: InnerState => InnerState): Option[GenericNode[EnumId, InnerState]]

  /**
   * Get the inner state of the State Machine node
   * @return inner state
   */
  def getState(): InnerState

  /**
   * Get the inner id of the State Machine node
   * @return id of the State Machine node
   */
  def getId(): EnumId

  /**
   * Tells, if state machine conditions hold true
   * @return true if inner conditions hold true, otherwise false
   */
  def isValid(): Boolean
}
