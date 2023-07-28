package biz.lobachev.annette.data_dictionary.builder.model

object Attributes {
  def findEntityAttribute(entity: Entity, domain: Domain, key: String): Option[String] =
    entity.attributes.find(a => a.key == key) match {
      case Some(attr) => Some(attr.value)
      case None       => findGroupAttribute(domain.groups(entity.groupId), domain, key)
    }

  def findGroupAttribute(group: Group, domain: Domain, key: String): Option[String] =
    group.attributes.find(a => a.key == key) match {
      case Some(attr) => Some(attr.value)
      case None       => findDomainAttribute(domain, key)
    }

  def findDomainAttribute(domain: Domain, key: String): Option[String] =
    domain.attributes.find(a => a.key == key).map(_.value)

}
