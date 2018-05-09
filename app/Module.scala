import com.google.inject.AbstractModule
import dao.{ WeightPointDAO, WeightPointSqliteDAO }

class Module extends AbstractModule {

  override def configure() = {
    // DAO object to persist WeightPoints
    bind(classOf[WeightPointDAO]).to(classOf[WeightPointSqliteDAO])
  }

}
