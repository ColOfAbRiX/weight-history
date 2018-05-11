package dao

import model.WeightPoint

import scala.util.Try

trait WeightPointDAO {

  def fetchAll(start: Option[String], end: Option[String]): Try[List[WeightPoint]]
  def fetchWeekly(start: Option[String], end: Option[String]): Try[List[WeightPoint]]
  def insert(weight: WeightPoint): Try[Unit]
  def update(weight: WeightPoint): Try[Unit]
  def delete(date: String): Try[Unit]

}