package models

object Helper {
  def checkAndReturn[T](objects: Seq[T], field: String): Option[T] ={
    objects.size match {
      case 0 => None
      case 1 => Some(objects.head)
      case _ => throw new Exception("Data integrity error: more than one entity with same "+field)
    }
  }
}
