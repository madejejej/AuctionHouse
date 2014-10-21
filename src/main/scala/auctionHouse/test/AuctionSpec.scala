package auctionHouse.test

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.WordSpecLike

import akka.actor.ActorSystem
import akka.testkit.TestFSMRef
import akka.testkit.TestKit
import auctionHouse.Auction
import auctionHouse.State
import auctionHouse.NewAuction
 
class AuctionSpec
  extends TestKit(ActorSystem("testSystem"))
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  val fsm = TestFSMRef(new Auction)
  
  "Auction" must {
    "start in NewAuction state" in {
    }
  }
}