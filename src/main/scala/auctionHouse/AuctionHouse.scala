package auctionHouse

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ReceiveTimeout
import akka.event.LoggingReceive
import scala.concurrent.duration.Duration

class AuctionHouse extends Actor {
  val auctions = for( i <- List.range(0, 3)) yield context.actorOf(Props[Auction], "auction" + (i+1)) 
  
  val buyers = for( i <- List.range(0, 5)) yield context.actorOf(Props(classOf[Buyer], auctions), "buyer" + (i+1)) 
  auctions.map(_ ! Start(BidTimer(8)))
  
  buyers.map(_ ! BidRandomAuction)
  
  context.setReceiveTimeout(Duration(2000, "millis"))
  
	def receive = LoggingReceive {
	  case AuctionEnded =>
	    println("Auction ended")
	  case ReceiveTimeout =>
	    buyers.map(_ ! BidRandomAuction)
	}
}