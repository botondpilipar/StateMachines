package StateMachineOptions

sealed trait TransitionOption extends StateMachineOption {

}

case object ManualTransition extends TransitionOption {}
case object AutomaticTransition extends TransitionOption {}
