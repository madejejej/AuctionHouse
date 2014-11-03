package auctionHouse

import akka.actor.ActorRef

sealed trait AuctionMessage

case class Start(var bidTimer: BidTimer) extends AuctionMessage
case class BidTimerExpired extends AuctionMessage
case class DeleteTimerExpired extends AuctionMessage
case class Bid(var bidAmount:Integer) extends AuctionMessage
case class Relist extends AuctionMessage

sealed trait AuctionStatus

case class AuctionEnded extends AuctionStatus
case class AuctionWon extends AuctionStatus

sealed trait BuyerMessage
case class BidRandomAuction extends BuyerMessage

sealed trait SellerMessage
case class ItemSold extends SellerMessage
case class ItemNotSold extends SellerMessage

sealed trait AuctionSearchMessage
case class CreateAuction(item: String) extends AuctionSearchMessage
case class SearchForKeyword(keyword: String) extends AuctionSearchMessage
case class MatchingAuctions(auctions: Vector[ActorRef]) extends AuctionSearchMessage