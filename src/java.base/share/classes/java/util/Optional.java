/*
 * Copyright (c) 2012, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.util;

import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.checkerframework.checker.optional.qual.EnsuresPresent;
import org.checkerframework.checker.optional.qual.EnsuresPresentIf;
import org.checkerframework.checker.optional.qual.OptionalCreator;
import org.checkerframework.checker.optional.qual.OptionalEliminator;
import org.checkerframework.checker.optional.qual.OptionalPropagator;
import org.checkerframework.checker.optional.qual.Present;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import org.checkerframework.framework.qual.AnnotatedFor;
import org.checkerframework.framework.qual.CFComment;
import org.checkerframework.framework.qual.Covariant;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container object which may or may not contain a non-{@code null} value.
 * If a value is present, {@code isPresent()} returns {@code true}. If no
 * value is present, the object is considered <i>empty</i> and
 * {@code isPresent()} returns {@code false}.
 *
 * <p>Additional methods that depend on the presence or absence of a contained
 * value are provided, such as {@link #orElse(Object) orElse()}
 * (returns a default value if no value is present) and
 * {@link #ifPresent(Consumer) ifPresent()} (performs an
 * action if a value is present).
 *
 * <p>This is a <a href="{@docRoot}/java.base/java/lang/doc-files/ValueBased.html">value-based</a>
 * class; programmers should treat instances that are
 * {@linkplain #equals(Object) equal} as interchangeable and should not
 * use instances for synchronization, or unpredictable behavior may
 * occur. For example, in a future release, synchronization may fail.
 *
 * @apiNote
 * {@code Optional} is primarily intended for use as a method return type where
 * there is a clear need to represent "no result," and where using {@code null}
 * is likely to cause errors. A variable whose type is {@code Optional} should
 * never itself be {@code null}; it should always point to an {@code Optional}
 * instance.
 *
 * @param <T> the type of value
 * @since 1.8
 */
@CFComment({"nullness :",
"The @NonNull annotation on the class makes the type \"@Nullable Optional<T>\" illegal and enforces",
"\"Rule #1: Never, ever, use null for an Optional variable or return value.\" from",
"https://stuartmarks.files.wordpress.com/2016/09/optionalmotherofallbikesheds3.pdf, which is",
"generally accepted practice.  If you wish to permit the type \"@Nullable Optional\", you may do so",
"by writing a stub file that overrides this class in the annotated JDK.",
"The type argument to Optional is meaningless.",
"Optional<@NonNull String> and Optional<@Nullable String> have the same",
"meaning, but are unrelated by the Java type hierarchy.",
"@Covariant makes Optional<@NonNull String> a subtype of Optional<@Nullable String>."
})
@AnnotatedFor({"lock", "nullness", "optional"})
@Covariant(0)
@jdk.internal.ValueBased
public final @NonNull class Optional<T> {
    /**
     * Common instance for {@code empty()}.
     */
    private static final Optional<?> EMPTY = new Optional<>(null);

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final @Nullable T value;

    /**
     * Returns an empty {@code Optional} instance.  No value is present for this
     * {@code Optional}.
     *
     * @apiNote
     * Though it may be tempting to do so, avoid testing if an object is empty
     * by comparing with {@code ==} or {@code !=} against instances returned by
     * {@code Optional.empty()}.  There is no guarantee that it is a singleton.
     * Instead, use {@link #isEmpty()} or {@link #isPresent()}.
     *
     * @param <T> The type of the non-existent value
     * @return an empty {@code Optional}
     */
    @OptionalCreator
    @Pure
    public static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the described value.
     *
     * @param value the value to describe; it's the caller's responsibility to
     *        ensure the value is non-{@code null} unless creating the singleton
     *        instance returned by {@code empty()}.
     */
    private Optional(@NonNull T value) {
        this.value = value;
    }

    /**
     * Returns an {@code Optional} describing the given non-{@code null}
     * value.
     *
     * @param value the value to describe, which must be non-{@code null}
     * @param <T> the type of the value
     * @return an {@code Optional} with the value present
     * @throws NullPointerException if value is {@code null}
     */
    @OptionalCreator
    @SideEffectFree
    public static <T> @Present Optional<T> of(@NonNull T value) {
        return new Optional<>(Objects.requireNonNull(value));
    }

    /**
     * Returns an {@code Optional} describing the given value, if
     * non-{@code null}, otherwise returns an empty {@code Optional}.
     *
     * @param value the possibly-{@code null} value to describe
     * @param <T> the type of the value
     * @return an {@code Optional} with a present value if the specified value
     *         is non-{@code null}, otherwise an empty {@code Optional}
     */
    @OptionalCreator
    @SideEffectFree
    @SuppressWarnings("unchecked")
    public static <T> Optional<@NonNull T> ofNullable(@Nullable T value) {
        return value == null ? (Optional<T>) EMPTY
                             : new Optional<>(value);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @apiNote
     * The preferred alternative to this method is {@link #orElseThrow()}.
     *
     * @return the non-{@code null} value described by this {@code Optional}
     * @throws NoSuchElementException if no value is present
     */
    @OptionalEliminator
    @Pure
    public @NonNull T get(@Present Optional<T> this) {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * If a value is present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    @OptionalEliminator
    @Pure
    @EnsuresPresentIf(result = true, expression = "this")
    public boolean isPresent() {
        return value != null;
    }

    /**
     * If a value is  not present, returns {@code true}, otherwise
     * {@code false}.
     *
     * @return  {@code true} if a value is not present, otherwise {@code false}
     * @since   11
     */
    @Pure
    @EnsuresPresentIf(result = false, expression = "this")
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     * @throws NullPointerException if value is present and the given action is
     *         {@code null}
     */
    @OptionalEliminator
    public void ifPresent(Consumer<? super @NonNull T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param action the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is
     *        present
     * @throws NullPointerException if a value is present and the given action
     *         is {@code null}, or no value is present and the given empty-based
     *         action is {@code null}.
     * @since 9
     */
    @OptionalEliminator
    public void ifPresentOrElse(Consumer<? super @NonNull T> action, Runnable emptyAction) {
        if (value != null) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an {@code Optional} describing the value, otherwise returns an
     * empty {@code Optional}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an {@code Optional} describing the value of this
     *         {@code Optional}, if a value is present and the value matches the
     *         given predicate, otherwise an empty {@code Optional}
     * @throws NullPointerException if the predicate is {@code null}
     */
    @OptionalPropagator
    public Optional<T> filter(Predicate<? super @NonNull T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }

    /**
     * If a value is present, returns an {@code Optional} describing (as if by
     * {@link #ofNullable}) the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code Optional}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code Optional}.
     *
     * @apiNote
     * This method supports post-processing on {@code Optional} values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of URIs, selects one that has not
     * yet been processed, and creates a path from that URI, returning
     * an {@code Optional<Path>}:
     *
     * <pre>{@code
     *     Optional<Path> p =
     *         uris.stream().filter(uri -> !isProcessedYet(uri))
     *                       .findFirst()
     *                       .map(Paths::get);
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code Optional<URI>}, and then
     * {@code map} returns an {@code Optional<Path>} for the desired
     * URI if one exists.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U> The type of the value returned from the mapping function
     * @return an {@code Optional} describing the result of applying a mapping
     *         function to the value of this {@code Optional}, if a value is
     *         present, otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    @CFComment({"@SideEffectFree: the mapper must not have side effects."})
    @OptionalPropagator
    @SideEffectFree
    public <U> Optional<U> map(Function<? super @NonNull T, ? extends @Nullable U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            return Optional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code Optional}-bearing mapping function to the value, otherwise returns
     * an empty {@code Optional}.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already an {@code Optional}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code Optional}.
     *
     * @param <U> The type of value of the {@code Optional} returned by the
     *            mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@code Optional}-bearing mapping
     *         function to the value of this {@code Optional}, if a value is
     *         present, otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is {@code null} or
     *         returns a {@code null} result
     */
    @OptionalPropagator
    public <U> Optional<U> flatMap(Function<? super @NonNull T, ? extends Optional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            Optional<U> r = (Optional<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a value is present, returns an {@code Optional} describing the value,
     * otherwise returns an {@code Optional} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Optional}
     *        to be returned
     * @return returns an {@code Optional} describing the value of this
     *         {@code Optional}, if a value is present, otherwise an
     *         {@code Optional} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    @OptionalPropagator
    public Optional<T> or(Supplier<? extends Optional<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isPresent()) {
            return this;
        } else {
            @SuppressWarnings("unchecked")
            Optional<T> r = (Optional<T>) supplier.get();
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @apiNote
     * This method can be used to transform a {@code Stream} of optional
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<Optional<T>> os = ..
     *     Stream<T> s = os.flatMap(Optional::stream)
     * }</pre>
     *
     * @return the optional value as a {@code Stream}
     * @since 9
     */
    @SideEffectFree
    public Stream<@NonNull T> stream() {
        if (!isPresent()) {
            return Stream.empty();
        } else {
            return Stream.of(value);
        }
    }

    /**
     * If a value is present, returns the value, otherwise returns
     * {@code other}.
     *
     * @param other the value to be returned, if no value is present.
     *        May be {@code null}.
     * @return the value, if present, otherwise {@code other}
     */
    @OptionalEliminator
    @Pure
    public @PolyNull T orElse(@PolyNull T other) {
        return value != null ? value : other;
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the
     *         supplying function
     * @throws NullPointerException if no value is present and the supplying
     *         function is {@code null}
     */
    @OptionalEliminator
    public @PolyNull T orElseGet(Supplier<? extends @PolyNull T> supplier) {
        return value != null ? value : supplier.get();
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code Optional}
     * @throws NoSuchElementException if no value is present
     * @since 10
     */
    @OptionalEliminator
    @Pure
    @EnsuresPresent("this")
    public @NonNull T orElseThrow(@Present Optional<T> this) {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @apiNote
     * A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     *        exception to be thrown
     * @return the value, if present
     * @throws X if no value is present
     * @throws NullPointerException if no value is present and the exception
     *          supplying function is {@code null}
     */
    @CFComment({"optional: orElseThrow(Supplier) does not throw NoSuchElementException, so its receiver is @MaybePresent.",
                "Contrast with orElseThrow(), defined just above, whose receiver is @Present."})
    @EnsuresPresent("this")
    @OptionalEliminator
    public <X extends Throwable> @NonNull T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Optional}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code Optional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object
     *         otherwise {@code false}
     */
    @Override
    @Pure
    @EnsuresNonNullIf(expression="#1", result=true)
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof Optional<?> other
                && Objects.equals(value, other.value);
    }

    /**
     * Returns the hash code of the value, if present, otherwise {@code 0}
     * (zero) if no value is present.
     *
     * @return hash code value of the present value or {@code 0} if no value is
     *         present
     */
    @OptionalEliminator
    @Pure
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this {@code Optional}
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @implSpec
     * If a value is present the result must include its string representation
     * in the result.  Empty and present {@code Optional}s must be unambiguously
     * differentiable.
     *
     * @return the string representation of this instance
     */
    @SideEffectFree
    @Override
    public String toString() {
        return value != null
            ? String.format("Optional[%s]", value)
            : "Optional.empty";
    }
}
