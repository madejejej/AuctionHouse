package auctionHouse

import akka.actor.Actor
import akka.actor.ActorRef
import akka.event.LoggingReceive
import akka.actor.Props
import scala.concurrent.duration._

class AuctionSearch extends Actor {
	var auctions: Vector[(String, ActorRef)] = Vector.empty
	var auctionNames: Vector[String] = Vector.empty
	var totalAuctions: Integer = 0
	
	def receive = LoggingReceive {
	  case CreateAuction(item) =>
	    var auction: ActorRef = context.actorOf(Props[Auction], "auction_" + (totalAuctions))
	    totalAuctions = totalAuctions + 1
	    auctions = auctions :+ (item, auction)
	    println("AuctionSearch registered auction " + item)
	    auction ! Start(BidTimer(8 seconds))
	  case SearchForKeyword(keyword) =>
	    var matchedAuctions: Vector[ActorRef] = auctions.filter(_._1.split(" ").contains(keyword)).map { _._2}
	    sender ! MatchingAuctions(matchedAuctions)
	}
}