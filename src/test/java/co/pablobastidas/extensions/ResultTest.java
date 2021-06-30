package co.pablobastidas.extensions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResultTest {

  @Test
  @DisplayName("when creating a success without content, then isSuccess is true")
  void whenCreatingASuccessWithoutContentThenIsSuccessIsTrue() {
    var result = Result.success();

    assertThat(result).isNotNull();
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  @DisplayName("when creating success tests, then `isSuccess` is true")
  void whenCreatingSuccessTestsThenIsSuccessIsTrue() {
    var result = Result.success("Value");

    assertThat(result).isNotNull();
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.isFailure()).isFalse();
  }

  @Test
  @DisplayName("when creating a failures Result, then `isFailure` is true and `isSuccess` is false")
  void whenCreatingAFailuresResultThenIsFailureIsTrueAndIsSuccessIsFalse() {
    var result = Result.failure(new MockTestFailure());

    assertThat(result.isFailure()).isTrue();
    assertThat(result.isSuccess()).isFalse();
  }

  @Test
  @DisplayName("when unwrapping a success result, then the return is present and the content is the given")
  void whenUnwrappingASuccessResultThenTheResultIsPresentAndTheContentIsTheGiven() {
    var value = "Value";
    var givenResult = Result.success(value);

    final var result = givenResult.unwrap();

    assertThat(result).contains(value);
  }

  @Test
  @DisplayName("when unwrapping a failed result, then the return is empty")
  void whenUnwrappingAFailedResultThenTheReturnIsEmpty() {
    var result = Result.failure(new MockTestFailure());

    //noinspection ResultOfMethodCallIgnored
    assertThatExceptionOfType(NullPointerException.class)
      .isThrownBy(result::unwrap);
  }

  @Test
  @DisplayName("given a successful result, when failure is asked, then result is empty")
  void givenASuccessfulResultWhenFailureIsAskedThenResultIsEmpty() {
    var result = Result.success("Value");

    //noinspection ResultOfMethodCallIgnored
    assertThatExceptionOfType(NullPointerException.class)
      .isThrownBy(result::failure);
  }

  @Test
  @DisplayName("given a failed result, when failure is asked, then result contains the failure")
  void givenAFailedResultWhenFailureIsAskedThenResultContainsTheFailure() {
    var givenFailure = new MockTestFailure();

    var result = Result.failure(givenFailure);

    assertThat(result.failure())
      .isEqualTo(givenFailure);
  }

  @Test
  @DisplayName("given a success result, when string representation is requested, then result contains the value string representation")
  void givenASuccessResultWhenStringRepresentationIsRequestedThenResultContainsTheValueStringRepresentation() {
    var value = "Value";
    var givenResult = Result.success(value);

    var result = givenResult.toString();

    assertThat(result).contains("Success").contains(value);
  }

  @Test
  @DisplayName("given an empty success result, when string representation is requested, then result contain only Success message.")
  void givenAnEmptySuccessResultWhenStringRepresentationIsRequestedThenResultContainOnlySuccessMessage() {
    var givenResult = Result.success();

    var result = givenResult.toString();

    assertThat(result).contains("Success");
  }

  @Test
  @DisplayName("given a failed result, when string representation is requested, then result contains the failure string representation.")
  void givenAFailedResultWhenStringRepresentationIsRequestedThenResultContainsTheFailureStringRepresentation() {
    var failure = new MockTestFailure();
    var givenResult = Result.failure(failure);

    var result = givenResult.toString();

    assertThat(result).contains("Failure").contains(failure.toString());
  }

  @Test
  @DisplayName("given a success result, when map, then return is a result with the result of given consumer")
  void givenASuccessResultWhenMapThenReturnIsAResultWithTheResultOfGivenConsumer() {
    var givenResult = Result.success("Value");
    Function<String, String> givenConsumer = v -> String.format("Mapped %s", v);

    var result = givenResult.map(givenConsumer);

    assertThat(result).isNotNull();
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.unwrap()).isEqualTo("Mapped Value");
  }

  @Test
  @DisplayName("given a failed result, when map, then return is the failed result")
  void givenAFailedResultWhenMapThenReturnIsTheFailedResult() {
    var failure = new MockTestFailure();
    Result<String> givenResult = Result.failure(failure);
    Function<String, String> givenConsumer = v -> String.format("Mapped %s", v);

    var result = givenResult.map(givenConsumer);

    assertThat(result.isFailure()).isTrue();
    assertThat(result.failure()).isEqualTo(failure);
  }

  @Test
  @DisplayName("given a success result and a map thar returns a success result, when flatMap, result is OK")
  void givenASuccessResultAndAMapTharReturnsASuccessResultWhenFlatMapResultIsOk() {
    var givenResult = Result.success(1);
    Function<Integer, Result<String>> givenConsumer = val -> Result.success(String.format("Mapped %s", val));

    var result = givenResult.flatMap(givenConsumer);

    assertThat(result).isNotNull();
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.unwrap()).isEqualTo("Mapped 1");
  }

  @Test
  @DisplayName("given a failed result, when flat map, then result is in failure with same failure")
  void givenAFailedResultWhenFlatMapThenResultIsInFailureWithSameFailure() {
    var failure = new MockTestFailure();
    Result<Integer> givenResult = Result.failure(failure);
    Function<Integer, Result<String>> givenConsumer = val -> Result.success(String.format("Mapped %s", val));

    var result = givenResult.flatMap(givenConsumer);

    assertThat(result.isFailure()).isTrue();
    assertThat(result.failure()).isEqualTo(failure);
  }

  @Test
  @DisplayName("given a success result and a failed result consumer, when flatMap, result is in failure")
  void givenASuccessResultAndAFailedResultConsumerWhenFlatMapResultIsInFailure() {
    var givenResult = Result.success(1);

    var failure = new MockTestFailure();
    Function<Integer, Result<String>> givenConsumer = val -> Result.failure(failure);

    var result = givenResult.flatMap(givenConsumer);

    assertThat(result.isFailure()).isTrue();
    assertThat(result.failure()).isEqualTo(failure);
  }

  @Test
  @DisplayName("given a success result, when onFailure, then on failure function is not called")
  void givenASuccessResultWhenOnFailureThenOnFailureFunctionIsNotCalled() {
    var givenReturn = 1;
    var givenResult = Result.success(givenReturn);
    Function<Failure, Integer> givenFunction = failure -> 3;

    var result = givenResult.onFailure(givenFunction);

    assertThat(result).isNotNull();
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.unwrap()).isEqualTo(givenReturn);
  }

  @Test
  @DisplayName("given a failed result, when onFailure, then on failure function is called")
  void givenAFailedResultWhenOnFailureThenOnFailureFunctionIsCalled() {
    var givenReturn = 3;
    Result<Integer> givenResult = Result.failure(new MockTestFailure());
    Function<Failure, Integer> givenFunction = failure -> givenReturn;

    var result = givenResult.onFailure(givenFunction);

    assertThat(result.isSuccess()).isTrue();
    assertThat(result.unwrap()).isEqualTo(givenReturn);
  }

  @Test
  @SuppressWarnings("unchecked")
  @DisplayName("given a success result, when apply, then the action is called")
  void givenASuccessResultWhenApplyThenTheActionIsCalled() {
    var value = "Hello";
    var givenResult = Result.success(value);
    Consumer<String> consumer = mock(Consumer.class);

    var result = givenResult.apply(consumer);

    assertThat(result).isNotNull();
    assertThat(result.isSuccess()).isTrue();
    verify(consumer).accept(value);
  }

  @Test
  @SuppressWarnings("unchecked")
  @DisplayName("given a failed result, when apply, then the action is NOT called")
  void givenAFailedResultWhenApplyThenTheActionIsNotCalled() {
    Result<String> givenResult = Result.failure(new MockTestFailure());
    Consumer<String> consumer = mock(Consumer.class);

    var result = givenResult.apply(consumer);

    assertThat(result).isNotNull();
    assertThat(result.isFailure()).isTrue();
    verify(consumer, never()).accept(any());
  }
}
