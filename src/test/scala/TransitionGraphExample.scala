object TransitionGraphExample {
  def transition12(id: EnumIdExample, state: Int): Option[EnumIdExample] = {
    (id, state) match {
      case (EnumIdFirst, 4) => Some(EnumIdSecond)
      case (EnumIdFirst, 6) => Some(EnumIdThird)
      case (_, _) => None
    }
  }

  def transition23(id: EnumIdExample, state: Int): Option[EnumIdExample] = {
    (id, state) match {
      case (EnumIdSecond, 6) => Some(EnumIdThird)
      case (_, _) => None
    }
  }

  def transition3(id: EnumIdExample, state: Int): Option[EnumIdExample] = None
}
