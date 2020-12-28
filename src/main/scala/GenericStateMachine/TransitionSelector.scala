package GenericStateMachine

import StateMachineOptions._
import scala.util.Random

case object TransitionSelector {
  def select[EnumId, State](transitions: List[State => Option[EnumId]],
                            settings: TransitionSelectionOption,
                            state: State): Option[EnumId] = {
    var workingList: List[State => Option[EnumId]] = Nil
    settings match {
      case FirstMatches => {
        workingList = transitions.dropWhile(f => f(state).isEmpty)
      }

      case AnyMatches => {
        workingList = Random.shuffle(transitions).dropWhile(f => f(state).isEmpty)
      }
    }

    if(workingList.isEmpty) {
      None
    } else {
      workingList.head(state)
    }
  }
}

