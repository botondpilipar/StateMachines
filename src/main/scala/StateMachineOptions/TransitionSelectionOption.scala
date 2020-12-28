package StateMachineOptions

trait TransitionSelectionOption extends StateMachineOption {
}

case object AnyMatches extends TransitionSelectionOption
case object FirstMatches extends TransitionSelectionOption
