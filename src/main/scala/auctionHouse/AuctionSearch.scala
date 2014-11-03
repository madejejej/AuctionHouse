package auctionHouse

import akka.actor.Actor
import akka.event.LoggingReceive
import akka.actor.Props
import akka.actor.ActorRef


class AuctionSearch extends Actor {
	var auctions: Vector[(String, ActorRef)] = Vector.empty
	var auctionNames: Vector[String] = Vector.empty
	
	def receive = LoggingReceive {
	  case CreateAuction(item) =>
	    var auction: ActorRef = context.actorOf(Props[Auction], "auction_" + item) 
	    auctions = auctions :+ (item, auction)
	    auction ! Start(BidTimer(8))
	  case SearchForKeyword(keyword) =>
	    var matchedAuctions: Vector[ActorRef] = auctions.filter(_._1.matches(keyword)).map { _._2}
	    sender ! MatchingAuctions(matchedAuctions)
	}
}