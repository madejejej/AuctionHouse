package auctionHouse

import scala.PartialFunction._
import scala.collection._
import scala.concurrent.duration._

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.FSM

sealed trait State
case object NewAuction extends State
case object Created extends State
case object Activated extends State
case object Ignored extends State
case object Sold extends State

sealed trait Data
case class BidTimer(auctionLength: FiniteDuration) extends Data

class Auction extends Actor with FSM[State, Data] {
  val SECONDS_TO_DELETE = 3 seconds
  val MINIMUM_BID: Integer = 1
  var currentBid: Integer = 0
  var bidder: ActorRef = null
  var creator: ActorRef = null
  var auctionLength = 3 seconds;

  startWith(NewAuction, null)
  
  when(NewAuction) {
    case Event(Start(BidTimer(auctionLength)), _) => {
      this.auctionLength = auctionLength
      creator = sender
      log.debug("Starting new Auction")
      goto(Created)
    }
  }

  when(Created) {
    case Event(BidTimerExpired, _) => {
      log.debug("BidTimer expired")
      goto(Ignored)
    }
    
    case Event(Bid(bidAmount), _) if bidAmount > MINIMUM_BID => { 
      log.debug("Bid made: {}", bidAmount)
      currentBid = bidAmount
      bidder = sender
      goto(Activated)
    }
  }
  
  when(Ignored) {
    case Event(Relist, BidTimer(auctionLength)) => {
      log.debug("Auction relisted")
      goto(Created)
    }
    
    case Event(DeleteTimerExpired, _) => {
      log.debug("Auction ignored, deleting item")
      stop
    }
  }
  
  when(Activated) {
    case Event(Bid(bidAmount), _) if bidAmount > currentBid => {
      log.debug("Higher bid {} than current bid of {}", bidAmount, currentBid)
      currentBid = bidAmount
      bidder = sender
      stay
    }
    
    case Event(BidTimerExpired, _) => {
      log.debug("Auction finished!")
      goto(Sold)
    }
  }
  
  onTransition {
    case NewAuction -> Created =>
      setTimer("auctionCreatedEnds", BidTimerExpired, auctionLength, false)
    case Created -> Ignored =>
      setTimer("deleteAuction", DeleteTimerExpired, SECONDS_TO_DELETE, false)
    case Activated -> Sold =>
      setTimer("deleteAuction", DeleteTimerExpired, SECONDS_TO_DELETE, false)
      context.parent ! AuctionEnded
      bidder ! AuctionWon
  }
  
  when(Sold) {
    case Event(DeleteTimerExpired, _) => {
      log.debug("Deleting item...")
      stop
    }
  }


  whenUnhandled {
    case Event(e, s) => {
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
    }
  }
}