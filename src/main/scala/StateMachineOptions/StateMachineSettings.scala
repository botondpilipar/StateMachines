package StateMachineOptions

case class StateMachineSettings(transitionOption: TransitionOption,
                                selectionOption: TransitionSelectionOption,
                                preserveStateOnTransition: Boolean) {
}
