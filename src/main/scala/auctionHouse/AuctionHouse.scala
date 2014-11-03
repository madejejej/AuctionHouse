package auctionHouse

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ReceiveTimeout
import akka.event.LoggingReceive
import scala.concurrent.duration.Duration

class AuctionHouse extends Actor {
  val auctionSearch = context.actorOf(Props(classOf[AuctionSearch]), "auctionSearch")
  
  val sellerItems = List( 
      List("Audi A6 Diesel Manual", "Estimote beacons", "AGH KI Private Key"),
      List("Green pen with transparent ink", "Ruby Rocks", "Funny cat pictures"),
      List("A Rock", "A Thing", "Lazer", "Screen Saver with a lot of pandas") )
  
  val sellers = sellerItems.zipWithIndex.map { 
    case(itemsList, index) =>
      context.actorOf(Props(classOf[Seller], itemsList), "seller" + (index+1))
  }
  
  val possibleKeywords = sellerItems.reduceLeft[List[String]] { 
    (allItems, itemsPerSeller) =>
    allItems ::: itemsPerSeller
  }.flatMap {
    _.split(" ")
  }
  
  val buyers = for( i <- List.range(0, 1)) yield context.actorOf(Props(classOf[Buyer], possibleKeywords), "buyer" + (i+1)) 
  
  buyers.map(_ ! BidRandomAuction)
  
  context.setReceiveTimeout(Duration(2000, "millis"))
  
	def receive = LoggingReceive {
	  case AuctionEnded =>
	    println("Auction ended")
	  case ReceiveTimeout =>
	    buyers.map(_ ! BidRandomAuction)
	}
}