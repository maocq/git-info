package fabrica

import java.time.ZonedDateTime

import com.github.javafaker.Faker
import persistence.commit.CommitRecord
import persistence.diff.DiffRecord
import persistence.group.GroupRecord


trait Data {

  val faker = new Faker

  val commit = CommitRecord(faker.code().asin(), faker.code().asin(), ZonedDateTime.now(), faker.code().asin(), faker.name().title(), faker.name().name(),
    faker.name().firstName(), faker.internet().emailAddress(), ZonedDateTime.now(), faker.name().firstName(), faker.internet().emailAddress(), ZonedDateTime.now(), faker.number().randomDigit())

  val diff = DiffRecord(faker.number().randomDigit(), faker.file().fileName(), faker.file().fileName(), faker.code().asin(), faker.code().asin(),
    faker.bool().bool(), faker.bool().bool(), faker.bool().bool(), faker.lorem().paragraph(), faker.number().randomDigit(), faker.number().randomDigit(), faker.code().ean8())

  val group = GroupRecord(faker.number().randomDigit(), faker.name().title(), ZonedDateTime.now())

}
