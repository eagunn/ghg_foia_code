package infrastructure.config

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import spock.lang.Specification

/**
 * Created by alabdullahwi on 8/7/2015.
 */

@ContextConfiguration(locations='classpath:applicationContextDaoOnly.xml')
@TransactionConfiguration(transactionManager = "transactionManager")
abstract class FlightDaoTest extends Specification {
}
