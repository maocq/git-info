package implicits

import cats.data.EitherT

trait EitherTSyntax {
  implicit final def implicitEitherT[F[_], A, B](value: F[Either[A, B]]): EitherTWrapper[F, A, B] = new EitherTWrapper(value)
}

final class EitherTWrapper[F[_], A, B](value: F[Either[A, B]]) {

  def toEitherT: EitherT[F, A, B] = EitherT(value)
}
