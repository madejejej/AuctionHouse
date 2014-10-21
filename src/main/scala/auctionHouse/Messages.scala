package auctionHouse

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