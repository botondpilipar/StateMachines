package GenericStateMachine

trait GenericStateMachine[EnumId, InnerType] {

  /**
   * Get the current underlying id
   * @return Id of the state machine node if state is valid
   */
  def getCurrentId(): Option[EnumId]

  /**
   * Get the current underlying state
   * @return State of the state machine node if state is valid
   */
  def getCurrentState(): Option[InnerType]

  /**
   * Apply monadic operation on state machine resulting in a possibly
   * non-empty machine
   * @param f Monadic operation which will result in a state machine
   *          with different Id and State type
   * @tparam Id Type parameter for new state machine id
   * @tparam Inner Type parameter for new state machine inner type
   * @return State machine if the original was ready to transition
   */
  def flatMap[Id, Inner](f: GenericNode[EnumId, InnerType] => GenericStateMachine[Id, Inner]): Option[GenericStateMachine[Id, Inner]]

  /**
   * Map the underlying inner state of the state machine
   * @param f Map operation
   * @return Altered state machine if inner conditions hold true
   */
  def map(f: InnerType => InnerType) : Option[GenericStateMachine[EnumId, InnerType]]

  /**
   * Alter the underlying identifier
   * @param f Map operation which determines the new identifier
   * @return State Machine with "f" determined new identifier
   */
  def transition(f: (EnumId, InnerType) => Option[EnumId]): Option[GenericStateMachine[EnumId, InnerType]]
}
