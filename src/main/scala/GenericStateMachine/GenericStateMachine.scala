package GenericStateMachine

trait GenericStateMachine[EnumId, InnerType] {
  def getCurrentState(): Option[EnumId]

  def flatMap[Id, Inner](f: GenericNode[EnumId, InnerType] => GenericStateMachine[Id, Inner]): Option[GenericStateMachine[Id, Inner]]

  def mapInner(f: InnerType => InnerType) : Option[GenericStateMachine[EnumId, InnerType]]

  def transition(f: (EnumId, InnerType) => EnumId): Option[GenericStateMachine[EnumId, InnerType]]
}
