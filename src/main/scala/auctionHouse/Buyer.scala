package auctionHouse

import akka.actor.Actor
import akka.actor.ActorRef
import akka.event.LoggingReceive
import scala.util.Random

class Buyer(auctions: List[ActorRef]) extends Actor {
	def receive = LoggingReceive {
	  case AuctionWon =>
	    println("Woohoo! I won an item!")
	  case BidRandomAuction =>
	    var auction = auctions(Random.nextInt(auctions.length))
	    auction ! Bid(Math.abs(Random.nextInt()))
	}
	
}