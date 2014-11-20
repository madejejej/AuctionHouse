	package auctionHouse.test
	
	import org.scalatest.BeforeAndAfterAll
	import org.scalatest.Matchers
	import org.scalatest.WordSpecLike
	import akka.actor.ActorSystem
	import akka.testkit.TestFSMRef
	import akka.testkit.TestKit
	import auctionHouse._
	import scala.concurrent.duration._
	import org.scalatest.concurrent.Timeouts
	import akka.testkit.ImplicitSender
 
class AuctionSpec
  extends TestKit(ActorSystem("testSystem"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {
	   
  "Auction" must {
    "start in NewAuction state" in {
      val fsm = TestFSMRef(new Auction)
      fsm.stateName should be (NewAuction)
    }
    
    "receive Start to be counted as a started auction" in {
      val fsm = TestFSMRef(new Auction)
      fsm ! Start(BidTimer(2 seconds))
      fsm.stateName should be (Created)
    }
    
    "go to ignored state when there was no bid" in {
      val fsm = TestFSMRef(new Auction)
      fsm ! Start(BidTimer(10 millis))
      awaitAssert(fsm.stateName should be (Ignored), 50 millis, 10 millis)
    }
    
    "get stopped when ignored" in {
      val fsm = TestFSMRef(new Auction)
      fsm.setState((Ignored))
      fsm.setTimer("deleteAuction", (DeleteTimerExpired), 10 millis, false)
      awaitAssert(
        fsm.isTerminated should be (true),
        50 millis,
        10 millis
      )
    }
    
    "go to activated state when there was a bid" in {
      val fsm = TestFSMRef(new Auction)
      fsm ! Start(BidTimer(5 seconds))
      fsm ! Bid(10)
      fsm.stateName should be (Activated)
      
    }
    
    "notify the winner" in {
      val fsm = TestFSMRef(new Auction)
      fsm.setState(Activated)
      fsm.setTimer("auctionCreatedEnds", BidTimerExpired, 20 millis, false)
      fsm ! Bid(10)
      expectMsg(50 millis, AuctionWon)
    }
    
    "get stopped when sold" in {
      val fsm = TestFSMRef(new Auction)
      fsm.setState((Sold))
      fsm.setTimer("deleteAuction", (DeleteTimerExpired), 10 millis, false)
      awaitAssert(
        fsm.isTerminated should be (true),
        50 millis,
        10 millis
      )
    }
  }
}