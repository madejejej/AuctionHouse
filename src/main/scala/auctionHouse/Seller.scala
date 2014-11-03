package auctionHouse

import akka.actor.Actor
import akka.event.LoggingReceive

class Seller(items: List[String]) extends Actor {
  
	println("Auctionsearch:" + context.actorSelection("../auctionSearch"))
	items.foreach(context.actorSelection("../auctionSearch") ! CreateAuction(_)) 
  
	def receive = LoggingReceive {
	  case ItemSold =>
	    println("Woohoo! I sold an item!")
	  case ItemNotSold =>
	    println("I didn't manage to sell an item :(")
	}
}