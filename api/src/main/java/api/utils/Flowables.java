package api.utils;

import io.reactivex.Flowable;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;

import java.util.function.Function;

public class Flowables {

    public static <I, R> Either<Throwable, Tuple2<I, R>> combine(Either<Throwable, I> e1, Either<Throwable, R> e2) {
        return e1.flatMap(l -> e2.flatMap(r -> Either.right(Tuple.of(l, r))));
    }

    public static <I, Rp> Flowable<Either<Throwable, Rp>> flatMap(Either<Throwable, I> input, Function<I, Flowable<Either<Throwable, Rp>>> mapper) {
        return input.fold(
                error -> Flowable.just(Either.left(error)),
                mapper::apply);
    }

}
