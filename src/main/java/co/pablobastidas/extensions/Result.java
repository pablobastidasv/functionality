package co.pablobastidas.extensions;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Result<T> {

  private final T value;
  private final co.pablobastidas.extensions.Failure failure;

  private Result(T value, co.pablobastidas.extensions.Failure failure) {
    this.value = value;
    this.failure = failure;
  }

  public static <T> Result<T> success(T value) {
    return new Result<>(value, null);
  }

  public static <T> Result<T> failure(co.pablobastidas.extensions.Failure failure) {
    return new Result<>(null, failure);
  }

  public static Result<Void> success() {
    return new Result<>(null, null);
  }

  public static Result<Map<String, ?>> join(Map<String, Result<?>> resultMap) {
    for (Result<?> value : resultMap.values()) {
      if (value.isFailure()) {
        return Result.failure(value.failure());
      }
    }
    return Result.success(resultMap.entrySet()
      .stream()
      .collect(
        Collectors.toMap(Entry::getKey, e -> e.getValue().unwrap())
      ));
  }

  public boolean isSuccess() {
    return isNull(failure);
  }

  public boolean isFailure() {
    return nonNull(failure);
  }

  public T unwrap() {
    requireNonNull(value);
    return value;
  }

  public Failure failure() {
    Objects.requireNonNull(failure);
    return failure;
  }

  @Override
  public String toString() {
    if (isSuccess()) {
      return ofNullable(value)
        .map(v -> String.format("Success(%s)!!!", v))
        .orElse("Success!!!");
    }
    return String.format("Failure(%s)", failure.toString());
  }

  public <R> Result<R> map(Function<T, R> action) {
    if (isSuccess()) {
      var mappedValue = action.apply(value);
      return Result.success(mappedValue);
    }
    return new Result<>(null, failure);
  }

  public <R> Result<R> flatMap(Function<T, Result<R>> action) {
    if (isSuccess()) {
      return action.apply(value);
    }
    return new Result<>(null, failure);
  }

  public Result<T> onFailure(Function<co.pablobastidas.extensions.Failure, T> action) {
    if (isSuccess()) {
      return this;
    }
    return Result.success(action.apply(failure));
  }

  public Result<T> apply(Consumer<T> action) {
    if (isSuccess()) {
      action.accept(value);
    }
    return this;
  }
}
