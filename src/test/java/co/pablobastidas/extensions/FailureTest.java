package co.pablobastidas.extensions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FailureTest {

  private static Stream<Arguments> nullFailureInitValues() {
    return Stream.of(
      Arguments.of(null, null),
      Arguments.of(null, "A falsis, heuretes bassus victrix."),
      Arguments.of("TEST", null)
    );
  }

  @ParameterizedTest
  @MethodSource("nullFailureInitValues")
  @DisplayName("given wrong code and/or message, when instantiate a new failure, then exception is thrown")
  void givenWrongCodeAndOrMessageWhenInstantiateANewFailureThenExceptionIsThrown(
    String givenCode,
    String givenMessage
  ) {
    assertThatExceptionOfType(NullPointerException.class)
      .isThrownBy(() -> new EmptyTestFailure(givenCode, givenMessage));
  }

  @Test
  @DisplayName("given a failure, when string representation is call, then string contains it's code and message")
  void givenAFailureWhenStringRepresentationIsCallThenStringContainsItSCodeAndMessage() {
    var code = "TEST";
    var message = "Humani generiss sunt toruss de ferox rector.";
    var failure = new EmptyTestFailure(code, message);

    var result = failure.toString();

    assertThat(result).contains(code, message);
  }

  @Test
  @DisplayName("given 2 failures with same code and message, when equals is called, then return is true")
  void given2FailuresWithSameCodeAndMessageWhenEqualsIsCalledThenReturnIsTrue() {
    var code = "TEST";
    var message = "Humani generiss sunt toruss de ferox rector.";
    var failure1 = new EmptyTestFailure(code, message);
    var failure2 = new EmptyTestFailure(code, message);

    var result = failure1.equals(failure2);

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("given 2 failures with different code, when equals is called, then resturn is false")
  void given2FailuresWithDifferentCodeWhenEqualsIsCalledThenResturnIsFalse() {
    var message = "Humani generiss sunt toruss de ferox rector.";
    var failure1 = new EmptyTestFailure("TEST1", message);
    var failure2 = new EmptyTestFailure("TEST2", message);

    var result = failure1.equals(failure2);

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("given 2 failures with different message, when equals is called, then result is false")
  void given2FailuresWithDifferentMessageWhenEqualsIsCalledThenResultIsFalse() {
    var code = "CODE";
    var failure1 = new EmptyTestFailure(code, "Canis, fuga, et scutum.");
    var failure2 = new EmptyTestFailure(code, "Nunquam locus quadra.");

    var result = failure1.equals(failure2);

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("given 2 failures with different message and code, when equals is called, then result is false")
  void given2FailuresWithDifferentMessageAndCodeWhenEqualsIsCalledThenResultIsFalse() {
    var failure1 = new EmptyTestFailure("CODE", "Canis, fuga, et scutum.");
    var failure2 = new EmptyTestFailure("TEST", "Nunquam locus quadra.");

    var result = failure1.equals(failure2);

    assertThat(result).isFalse();
  }

  private static class EmptyTestFailure extends Failure {

    public EmptyTestFailure(String code, String message) {
      super(code, message);
    }
  }
}
