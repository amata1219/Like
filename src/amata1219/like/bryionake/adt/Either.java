package amata1219.like.bryionake.adt;

import java.util.function.Function;

public abstract class Either<L, R> {

    public static <L, R> Either<L, R> success(R value) {
        return new Success<>(value);
    }

    public static <L, R> Either<L, R> failure(L error) {
        return new Failure<>(error);
    }

    public abstract <T> Either<L, T> flatMap(Function<R, Either<L, T>> mapper);

    public static class Success<L, R> extends Either<L, R> {

        public final R value;

        private Success(R value) {
            this.value = value;
        }

        @Override
        public <T> Either<L, T> flatMap(Function<R, Either<L, T>> mapper) {
            return mapper.apply(value);
        }
    }

    public static class Failure<L, R> extends Either<L, R> {

        public final L error;

        private Failure(L error) {
            this.error = error;
        }

        @Override
        public <T> Either<L, T> flatMap(Function<R, Either<L, T>> mapper) {
            return (Either<L, T>) this;
        }
    }

}
