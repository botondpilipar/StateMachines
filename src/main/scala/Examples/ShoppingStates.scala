package Examples

sealed trait ShoppingStates {

}

case object Checkout extends ShoppingStates
case object EmptyCart extends ShoppingStates
case object Shopping extends ShoppingStates
